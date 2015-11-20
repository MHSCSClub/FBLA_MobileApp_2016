<?php

	/*
		ISignal class
		Server return data payload representation

		status: bool (indicates success/error)
		data: payload data
		message: success/error message
	*/

	class ISignal {

		private $status;
		private $data;
		private $message;

		public function __construct($status, $data, $message) {
			$this->status = $status;
			$this->data = $data;
			$this->message = $message;
		}

		public function isError() {
			return $this->type;
		}

		public function setData($data) {
			$this->data = $data;
		}

		public function toJSON() {
			$arr = array("status" => $status, "data" => $data, "message" => $message);
			return json_encode($arr);
		}
	}

	/*
		Commonly used signals collection
	*/

	class Signal {
		public static $dbConnectionError;
		public static $authenticationError;
		public static $success;
	}

	Signal::$dbConnectionError = new ISignal(False, NULL, "Database connection error");
	Signal::$authenticationError = new ISignal(False, NULL, "Authentication error");
	Signal::$success = new ISignal(True, NULL, "Generic success");
?>