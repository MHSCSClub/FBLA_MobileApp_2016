<?php
	
	require_once("requestHandler.class.php");
	require_once("signal.class.php");
	require_once("dataAccess.class.php");

	$user_request = $_GET['request'];

	function notFound() {
		header("HTTP/1.0 404 Not Found");
		echo "<html><body><h1>Page not found.</h1></body></html>";
		die();
	}

	//Using RequestHandler class, look in class to find documentation
	$RH = new RequestHandler();
	$WC = $RH->getWildcard();

	$RH->D("", "test");

	// test/get
	$RH->F("test", "get", function() {
		return Signal::success();
	});
	// test/post
	$RH->F("test", "post", function() {
		$foo = $_POST["foo"];
		$ret = NULL;
		
		if(isset($foo)) {
			$data = array("fooback" => $foo);
			return Signal::success()->setData($data);
		} else {
			$ret = Signal::error()->setMessage("foo parameter not set error");
		}

		return $ret;
	});
	// test/file
	$RH->F("test", "file", function() {
		$fdata = file_get_contents($_FILES['test']['tmp_name']);

		$back = array("sha256" => hash("sha256", $fdata));
		return Signal::success()->setData($back);
	});

	$RH->D("", "user");

	// user/register
	$RH->F("user", "register", function() {
		$username = $_POST["username"];
		$password = $_POST["password"];
		return DataAccess::register($username, $password);
	});
	// user/login
	$RH->F("user", "login", function() {
		$username = $_POST["username"];
		$password = $_POST["password"];
		return DataAccess::login($username, $password);
	});
	// user/verify
	$RH->F("user", "verify", function() {
		return DataAccess::authGet($_GET['authcode'], "verify");
	});
	// user/info
	$RH->F("user", "info", function() {
		return DataAccess::authGet($_GET['authcode'], "info");
	});
	// user/logout
	$RH->F("user", "logout", function() {
		return DataAccess::authGet($_GET['authcode'], "logout");
	});

	$RH->D("", "picture");

	// picture/upload
	$RH->F("picture", "upload", function() {
		$params = array();
		$params['geolat']= $_GET['geolat'];
		$params['geolong'] = $_GET['geolong'];
		$params['picdata'] = file_get_contents($_FILES['picture']['tmp_name']);

		return DataAccess::authPost($_GET['authcode'], "picupload", $params);
	});
	// picture/fetch
	$RH->F("picture", "fetch", function() {
		$params = array();

		$params['geolat']= $_GET['geolat'];
		$params['geolong'] = $_GET['geolong'];
		$params['amount'] = $_GET['amount'];

		$params['distance'] = $_GET['ft_dist'];
		$params['time'] = $_GET['ft_time']

		return DataAccess::authPost($_GET['authcode'], "picfetch", $params);
	});
	// picture/* GET/DELETE
	$RH->F("picture", $WC, function($id) {
		//todo
	});

	try {
		$response = $RH->call($user_request);

		header('Content-Type: application/json');
		echo $response->toJSON();
	} catch(Exception $e) {
		notFound();
	}

?>