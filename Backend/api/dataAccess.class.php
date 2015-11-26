<?php

	require_once("secret.class.php");
	require_once("signal.class.php");
	require_once("exception.class.php");

	/*
		Database interface
		
		Public interface:

		Return value is ALWAYS an object of type ISignal

		register($username, $password): registers users in database

		login($username, $password): logs in + authcode

		authGET($authcode, $function): calls a function, no additional data

		authPOST($authcode, $function, $params): $params is an array

	*/

	class DataAccess
	{

		private static $ip = "localhost";
		private static $dbName = "fbla2016";

		/*
			Public interface
		*/

		public static function register($username, $password) {
			return self::run(function() use ($username, $password) {
				return DataAccess::REAL_register($username, $password);
			});
		}

		public static function login($username, $password) {
			return self::run(function() use ($username, $password) {
				return DataAccess::REAL_login($username, $password);
			});
		}

		public static function authGET($authcode, $funcname) {
			return self::run(function() use ($authcode, $funcname) {
				$realfunc = "DataAccess::GET_$funcname";
				$db = self::getConnection();
				$userid = DataAccess::authSetup($db, $authcode);

				return call_user_func($realfunc, $db, $userid);
			});
		}

		public static function authPOST($authcode, $funcname, $params) {
			return self::run(function() use ($authcode, $funcname) {
				$realfunc = "DataAccess::POST_$funcname";
				$db = self::getConnection();
				$userid = DataAccess::authSetup($db, $authcode);

				return call_user_func($realfunc, $db, $userid, $params);
			});
		}

		/*
			Private methods

			All functions are ran through the run() method
			This ensures consistent error handling

		*/

		private static function run($function) {
			try {
				return $function();
			} catch(DBConnectException $e) {
				return Signal::dbConnectionError();
			} catch(AuthException $e) {
				return Signal::authError();
			} catch(Exception $e) {
				return Signal::error()->setMessage($e->getMessage());
			}
		}

		private static function getConnection() {
			$db = new mysqli(self::$ip, Secret::$username, Secret::$password, self::$dbName);

			if($db->connect_error)
				throw new DBConnectException();

			return $db;
		}

		private static function hash($value) {
			return hash("sha256", $value);
		}

		private static function hashPass($pass, $salt) {
			self::hash($pass.$salt);
		}

		/*
			Helper functions that interface with the database
		*/

		private static function authSetup($db, $authcode) {
			//Get userid from auth
			$stmt = $db->prepare("SELECT userid FROM auth WHERE authcode=? AND NOW() < expire");
			$stmt->bind_param('s', $authcode);
			$stmt->execute();
			$res = $stmt->get_result();
			$stmt->close();

			if($res->num_rows != 1)
				throw new AuthException();

			$userid = $res->fetch_assoc()['userid'];

			//Update authcode expiration
			self::updateAuthExpiration($userid);
			return $userid;
		}

		private static function updateAuthExpiration($userid) {
			$db = self::getConnection();
			$db->query("UPDATE auth SET expire=DATE_ADD(NOW(), INTERVAL 1 MONTH) WHERE userid=$userid");
		}

		/*
			All actions
		*/

		private static function REAL_register($username, $password) {
			$db = self::getConnection();

			//Verify basic UN + Pass checks
			//UN >= 4 chars, Pass >= 8 chars
			if(strlen($username) < 5 || strlen($password) < 8)
				throw new Exception("Parameter length error");

			//Check if user exists
			$stmt = $db->prepare('SELECT userid FROM users WHERE username=?');
			$stmt->bind_param('s', $username);
			$stmt->execute();
			$res = $stmt->get_result();
			if($res->num_rows > 0)
				throw new Exception("Username already taken");
			$stmt->close();

			//Process password: generate salt and hash pwd + salt
			$random = openssl_random_pseudo_bytes(64);
			$salt = self::hash($random);
			$hshpass = self::hashPass($password, $salt);

			//Insert user into database
			$stmt = $db->prepare('INSERT INTO users VALUES (null, ?, ?, ?)');
			$stmt->bind_param('sss', $username, $hshpass, $salt);
			$stmt->execute();
			$stmt->close();
			return Signal::success();
		}

		private static function REAL_login($username, $password) {
			$db = self::getConnection();

			//Fetch salt + check if user exists
			$stmt = $db->prepare('SELECT username, salt FROM users WHERE username=?');
			$stmt->bind_param('s', $username);
			$stmt->execute();
			$res = $stmt->get_result();
			$stmt->close();

			//User found (note same error)
			if($res->num_rows != 1)
				throw new Exception("Invalid credentials error");

			$row = $res->fetch_assoc();
			$username = $row['username']; //username is safe now: no risk of sql injection
			$salt = $row['salt'];

			//Salt password
			$hshpass = self::hashPass($password, $salt); //hshpass also safe, no sql injection in a hash
			$res = $db->query("SELECT userid FROM users WHERE username=$username AND password=$hshpass");

			//Authentication
			if($res->num_rows != 1)
				throw new Exception("Invalid credentials error");
			$userid = $res->fetch_assoc()["userid"];

			//Check if user in auth table
			$res = $db->query("SELECT authcode FROM auth WHERE userid=$userid");

			//Generate a random authcode
			$random = openssl_random_pseudo_bytes(64);
			$authcode = self::hash($random);

			if($res->num_rows >= 1) {
				self::updateAuthExpiration($userid);
				$authcode = $res->fetch_assoc()['authcode'];
			} else {
				$db->query("INSERT INTO auth VALUES ( null, $userid, '$authcode', DATE_ADD(NOW(), INTERVAL 1 MONTH) )");
			}

			//Return success with data
			$data = array("authcode" => $authcode);
			return Signal::success()->setData($data);
		}

		private static function GET_verify($db, $userid) {
			//If userid exists, it means that authcode is valid already
			return Signal::success();
		}

		private static function GET_info($db, $userid) {
			$db = self::getConnection();

			//Username
			$res = $db->query("SELECT username FROM users WHERE userid=?");
			$username = $res->fetch_assoc()["username"];

			//Data
			$data = array("username" => $username);
			return Signal::success()->setData($data);
		}

		private static function GET_logout($db, $userid) {
			$db = self::getConnection();

			//Remove authcode from table
			$db->query("DELETE FROM auth WHERE userid=$userid");
			return Signal::success();
		}

	}
?>
