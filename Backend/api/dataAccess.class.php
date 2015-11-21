<?php

	require_once("secret.class.php");
	require_once("signal.class.php");

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
		private $db;

		/*
			Public interface
		*/

		public function __construct($status, $data, $message) {
			$db = getConnection();
		}

		public function register($username, $password) {

		}

		public function login($username, $password) {

		}

		public function authGET($authcode, $function) {

		}

		public function authPOST($authcode, $function, $params) {

		}

		/*
			Private methods
		*/

		private function getConnection() {
			return new mysqli(DataAccess::$ip, Secret::$username, Secret::$password, "mpcp");
		}


	}
?>