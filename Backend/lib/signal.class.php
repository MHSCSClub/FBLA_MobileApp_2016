<?php

	/*
		ISignal class
		Server return data payload representation

		status: bool (indicates success/error)
		data:
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
		public function getMessage() {
			return $this->mes;
		}
	}

	/*
		Commonly used signals collection
	*/
	class Signal {
		public static $error;
		public static $dbConnectionError;
		public static $authenticationError;
		public static $success;
	}
	Signal::$error = new ISignal("Generic error", 0);
	Signal::$dbConnectionError = new ISignal("Database connection error", 0);
	Signal::$authenticationError = new ISignal("Authentication error", 0);
	Signal::$success = new ISignal("Generic success", 1);
?>