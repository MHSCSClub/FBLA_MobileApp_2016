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
			return self::run(function() use ($authcode, $funcname, $params) {
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
			} catch(IMGException $e) {
				return Signal::imgError()->setMessage($e->getMessage());
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
			return self::hash($pass.$salt);
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
			self::updateAuthExpiration($db, $userid);
			return $userid;
		}

		private static function updateAuthExpiration($db, $userid) {
			$db->query("UPDATE auth SET expire=DATE_ADD(NOW(), INTERVAL 1 MONTH) WHERE userid=$userid");
		}

		//Creates a JSON array out of multiple results
		private static function formatArrayResults($res) {
			//Format results
			$rows = array();
			while($r = $res->fetch_assoc()) {
				$rows[] = $r;
			}
			return Signal::success()->setData($rows);
		}

		/*
			All actions
		*/

		//User

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
			$res = $db->query("SELECT userid FROM users WHERE username='$username' AND password='$hshpass'");

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
				$authcode = $res->fetch_assoc()['authcode'];
			} else {
				//Set temporary expiration date and then update
				$db->query("INSERT INTO auth VALUES (null, $userid, '$authcode', NOW() )");
			}
			self::updateAuthExpiration($db, $userid);

			//Return success with data
			$data = array("authcode" => $authcode);
			return Signal::success()->setData($data);
		}

		private static function GET_verify($db, $userid) {
			//If userid exists, it means that authcode is valid already
			return Signal::success();
		}

		private static function GET_info($db, $userid) {
			//Username
			$res = $db->query("SELECT username FROM users WHERE userid=$userid");
			$username = $res->fetch_assoc()["username"];

			//Data
			$data = array("username" => $username);
			return Signal::success()->setData($data);
		}

		private static function GET_logout($db, $userid) {
			//Remove authcode from table
			$db->query("DELETE FROM auth WHERE userid=$userid");
			return Signal::success();
		}

		//Picture

		private static function POST_picupload($db, $userid, $params) {
			if(is_null($params['title']) || is_null($params['geolat']) || is_null($params['geolong']) || is_null($params['picdata']))
				throw new Exception("Invalid POST data");

			$stmt = $db->prepare("INSERT INTO pictures VALUES (null, $userid, ?, ?, ?, NOW(), 0, 0, ?)");
			$stmt->bind_param('sdds', $params['title'], $params['geolat'], $params['geolong'], $params['picdata']);
			$stmt->execute();
			$stmt->close();

			$res = $db->query("SELECT LAST_INSERT_ID()");
			$pid = $res->fetch_assoc()['LAST_INSERT_ID()'];

			$data = array("pid" => $pid);
			return Signal::success()->setData($data);
		}

		private static function POST_picfetch($db, $userid, $params) {
			//Distance function between two lats and long (Haversine function)
			//IN MILES
			$mylat = '?';
			$mylong = '?';
			$dist_func = 
				"3959 * acos (".
    				"  cos( radians($mylat) )".
    				"* cos( radians(geolat) )".
    				"* cos( radians(geolong) - radians($mylong) )".
    				"+ sin( radians($mylat) )".
    				"* sin( radians(geolat) )".
    			")"; 
			$userlat = $params["geolat"];
			$userlong = $params["geolong"];

			//Distance filter
			$userdist = 0;
			$distquery = ' dist >= ? ';
			if(isset($params['distance'])) {
				$userdist = $params['distance'];
				$distquery = ' dist <= ? ';
			}

			//Time filter
			$usertime = '1970-01-01 00:00:00';
			$timequery = ' created >= ? ';
			if(isset($params['time'])) {
				$usertime = $params['time'];
			}

			//Username filter
			$username = ' ';
			$namequery = ' username <> ? ';
			if(isset($params['name'])) {
				$username = $params['name'];
				$namequery = ' username = ? ';
			}

			//Views filter
			$userviews = 0;
			$viewquery = ' views >= ? ';
			if(isset($params['views'])) {
				$userviews = $params['views'];
				$viewquery = ' views <= ? ';
			}

			//Me filter
			$mequery = '';
			if(isset($params['me']) && $params['me']) {
				$mequery = " LEFT JOIN comments ON pictures.pid=comments.pid AND comments.userid=$userid WHERE comments.userid IS NULL AND pictures.userid<>$userid";
			}

			$query = "SELECT pictures.pid AS pid, title, geolat, geolong, created, $dist_func AS dist, username, (likes + dislikes) AS views FROM pictures INNER JOIN users ON pictures.userid = users.userid ".
						 "$mequery HAVING $distquery AND $timequery AND $namequery AND $viewquery ORDER BY dist";

			$stmt = $db->prepare($query);
			//throw new Exception($query);
			$stmt->bind_param('ddddssi', $userlat, $userlong, $userlat, $userdist, $usertime, $username, $userviews);
			$stmt->execute();

			$res = $stmt->get_result();
			return self::formatArrayResults($res);
		}

		private static function GET_picfetchme($db, $userid) {
			$res = $db->query("SELECT pid, title, geolat, geolong, created, likes, dislikes, (likes + dislikes) AS views FROM pictures WHERE userid=$userid ORDER BY created DESC");
			return self::formatArrayResults($res);
		}

		private static function POST_picfetchraw($db, $userid, $params) {
			$stmt = $db->prepare("SELECT data FROM pictures WHERE pid=?");
			$stmt->bind_param('i', $params["pid"]);
			$stmt->execute();

			$res = $stmt->get_result();

			if($res->num_rows != 1)
				throw new IMGException("Image not found");

			$imgdata = $res->fetch_assoc()['data'];
			return Signal::success()->setType("IMG")->setData($imgdata);
		}

		//Comments

		private static function POST_commentfetch($db, $userid, $params) {
			$stmt = $db->prepare("SELECT pid FROM pictures WHERE pid=? AND userid=$userid");
			$stmt->bind_param('i', $params["pid"]);
			$stmt->execute();

			$res = $stmt->get_result();
			if($res->num_rows != 1)
				throw new Exception("Invalid picture id or not your picture");
			$pid = $res->fetch_assoc()['pid'];

			$res = $db->query("SELECT username, comment, style FROM comments INNER JOIN users ON comments.userid = users.userid WHERE pid=$pid");
			return self::formatArrayResults($res);
		}

		private static function POST_commentcreate($db, $userid, $params) {
			if(is_null($params['like']))
				throw new Exception("Invalid POST data");

			//Style check
			if(!is_null($params['style']) && intval($params['style']) == 0)
				$params['style'] = 3;

			$stmt = $db->prepare("SELECT pid FROM pictures WHERE pid=?");
			$stmt->bind_param('i', $params["pid"]);
			$stmt->execute();

			$res = $stmt->get_result();
			if($res->num_rows != 1)
				throw new Exception("Invalid picture id");

			$pid = $res->fetch_assoc()['pid'];

			//Updates views (likes/dislikes) in pictures
			$lquery = 'likes=likes+1';
			if(!$params['like']) {
				$lquery = 'dislikes=dislikes+1';
			}
			$db->query("UPDATE pictures SET $lquery WHERE pid=$pid");

			//Create comment
			$stmt = $db->prepare("INSERT INTO comments VALUES (null, $pid, $userid, ?, ?)");
			$stmt->bind_param('si', $params["comment"], $params["style"]);
			$stmt->execute();

			return Signal::success();
		}

	}
?>
