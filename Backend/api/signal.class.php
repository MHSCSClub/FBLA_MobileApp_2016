<?php

	/*
		ISignal class
		Server return data payload representation

		status: success/error
		data: payload data
		message: success/error message
	*/

	class ISignal
	{

		private $status;
		private $data;
		private $message;

		public function __construct($status, $data, $message) {
			$this->status = $status;
			$this->data = $data;
			$this->message = $message;
		}

		public function isError() {
			return $this->status == "error";
		}

		public function setData($data) {
			$this->data = $data;
		}

		public function setMessage($message) {
			$this->message = $message;
		}

		public function toJSON() {
			$arr = array("status" => $status, "data" => $data, "message" => $message);
			return json_encode($arr);
		}
	}

	/*
		Commonly used signals collection
	*/

	class Signal
	{
		public static function error() {
			return new ISignal("error", NULL, "Generic error");
		}
		public static function dbConnectionError() {
			return new ISignal("error", NULL, "Database connection error");
		}
		public static function authError() {
			return new ISignal("error", NULL, "Authentication error");
		}

		public static function success() {
			return new ISignal("success", NULL, "Generic success");
		}
	}
?>