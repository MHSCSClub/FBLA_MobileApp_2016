<?php

	/*
		ISignal class
		Server return data payload representation

		status: bool (indicates success/error)
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
			return $this->type;
		}

		public function setData($data) {
			$this->data = $data;
		}

		public function setMessage($message) {
			$this->message = $message;
		}

		public function toJSON() {
			$rstat = $status == True ? "success" : "failure";
			$arr = array("status" => $rstat, "data" => $data, "message" => $message);
			return json_encode($arr);
		}
	}

	/*
		Commonly used signals collection
	*/

	class Signal
	{
		public static function error() {
			return new ISignal(False, NULL, "Generic error");
		}
		public static function dbConnectionError() {
			return new ISignal(False, NULL, "Database connection error");
		}
		public static function authError() {
			return new ISignal(False, NULL, "Authentication error");
		}

		public static function success() {
			return new ISignal(True, NULL, "Generic success");
		}
	}
?>