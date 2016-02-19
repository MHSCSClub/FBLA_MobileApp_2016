<?php
	
	require_once("requestHandler.class.php");
	require_once("signal.class.php");
	require_once("dataAccess.class.php");

	$user_request = $_GET['request'];

	function notFound() {
		header("HTTP/1.0 404 Not Found");
		echo("<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\"><html><head><title>404 Not Found</title></head><body><h1>Not Found</h1><p>The requested URL was not found on this server.</p></body></html>");
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
		$username = @$_POST["username"];
		$password = @$_POST["password"];
		return DataAccess::register($username, $password);
	});
	// user/login
	$RH->F("user", "login", function() {
		$username = @$_POST["username"];
		$password = @$_POST["password"];
		return DataAccess::login($username, $password);
	});
	// user/verify
	$RH->F("user", "verify", function() {
		return DataAccess::authGet(@$_GET['authcode'], "verify");
	});
	// user/info
	$RH->F("user", "info", function() {
		return DataAccess::authGet(@$_GET['authcode'], "info");
	});
	// user/logout
	$RH->F("user", "logout", function() {
		return DataAccess::authGet(@$_GET['authcode'], "logout");
	});

	$RH->D("", "picture");

	// picture/upload
	$RH->F("picture", "upload", function() {
		$params = array();
		$params['title'] = @$_POST['title'];
		$params['geolat']= @$_POST['geolat'];
		$params['geolong'] = @$_POST['geolong'];
		if(!isset($_FILES['picture'])) {
			return Signal::error()->setMessage("File not uploaded");
		}
		$params['picdata'] = file_get_contents($_FILES['picture']['tmp_name']);

		return DataAccess::authPost(@$_GET['authcode'], "picupload", $params);
	});
	// picture/fetch
	$RH->F("picture", "fetch", function() {
		$params = array();

		$params['geolat']= @$_GET['geolat'];
		$params['geolong'] = @$_GET['geolong'];

		$params['distance'] = @$_GET['ft_dist'];
		$params['time'] = @$_GET['ft_time'];
		$params['name'] = @$_GET['ft_name'];
		$params['views']= @$_GET['ft_views'];
		$params['me'] = @$_GET['ft_me'];

		return DataAccess::authPost(@$_GET['authcode'], "picfetch", $params);
	});
	// picture/* GET/DELETE
	$RH->F("picture", $WC, function($trace) {
		$params = array("pid" => $trace[1]);

		switch ($_SERVER['REQUEST_METHOD']) {
			case 'GET':
				return DataAccess::authPost(@$_GET['authcode'], "picfetchraw", $params);
				break;
			
			case 'DELETE':
				//todo
				break;
		}
		
		return Signal::error()->setMessage("Invalid request type");
	});
	//picture/fetch/me
	$RH->D("picture", "fetch");
	$RH->F("picture/fetch", "me", function($trace) {
		return DataAccess::authGet(@$_GET['authcode'], "picfetchme");
	});

	$RH->D("picture", $WC);

	// picture/*/comment
	$RH->F("picture/$WC", "comment", function($trace) {
		$params = array("pid" => $trace[1]);

		switch ($_SERVER['REQUEST_METHOD']) {
			case 'GET':
				return DataAccess::authPost(@$_GET['authcode'], "commentfetch", $params);
				break;

			case 'POST':
				$params['like'] = @$_POST['like'];
				$params['comment'] = @$_POST['comment'];
				$params['style'] = @$_POST['style'];
				return DataAccess::authPost(@$_GET['authcode'], "commentcreate", $params);
				break;
			
			case 'DELETE':
				//todo
				break;
		}
	});

	try {
		$response = $RH->call($user_request);

		switch ($response->getType()) {
			case 'JSON':
				header('Content-Type: application/json');
				echo json_encode($response->toArray());
				break;

			case 'IMG':

				//Return not found in case of error
				if($response->isError())
					notFound();
				ob_clean(); //Prevent stray new lines
				header('Content-Type: image/jpeg');
				echo $response->getData();
				break;
			
			default:
				throw new Exception();
				break;
		}
	} catch(Exception $e) {
		notFound();
	}

?>
