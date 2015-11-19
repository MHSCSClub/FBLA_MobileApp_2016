<?php

	header('Content-Type: application/json');

	$user_request = $_GET['request'];

	function notFound() {
		header("HTTP/1.0 404 Not Found");
		die();
	}

	/*
		Holds all request options. Example: /API/foo/bar/mhs (GET)
		Requires
			"foo" => array(
				"bar" => array(
					"mhs" => function() {
						......
					}
				)
			)
		
	*/
	$requestChoice = array(

		"test" => array(

			"get" => function() {

			},

			"post" => function() {

			}

		),

		"user" => array(

			"register" => function() {

			},

			"login" => function() {

			},

			"verify" => function() {

			},

			"info" => function() {

			},

			"logout" => function() {

			}
		)

	);

	//Divide user request into tokens
	$user_action = explode("/", $user_request);
	$ulen = count($user_action) - 1;

	//cd is like the cd command, specifies current directory, starts at root
	$cd = $requestChoice;
	for($i = 0; $i < $ulen; ++$i) {
		$cur = $cd[$user_action[$i]];
		if(isset($cur) && is_array($cur)) {
			$cd = $cur;
		} else {
			notFound();
		}
	}

	$user_func = $user_action[$ulen];

	//Check if user_func is actually a function
	if(!is_callable($cd[$user_func])) {
		notFound();
	}

	$ret = call_user_func($cd[$user_func]);
	echo json_encode($ret);
?>
