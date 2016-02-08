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
		private $type;

		const DEFAULT_TYPE = 'JSON';

		public function __construct($status, $data, $message, $type = self::DEFAULT_TYPE) {
			$this->status = $status;
			$this->data = $data;
			$this->message = $message;
			$this->type = $type;
		}

		public function isError() {
			return $this->status == "error";
		}

		public function setData($data) {
			$this->data = $data;
			return $this;
		}

		public function getData() {
			return $this->data;
		}

		public function setMessage($message) {
			$this->message = $message;
			return $this;
		}

		public function getMessage() {
			return $this->message();
		}

		public function setType($type) {
			$this->type = $type;
			return $this;
		}

		public function getType() {
			return $this->type;
		}

		public function toArray() {
			return array("status" => $this->status, "data" => $this->data, "message" => $this->message);
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
		public static function imgError() {
			return new ISignal("error", NULL, "Image error", "IMG");
		}

		public static function success() {
			return new ISignal("success", NULL, "Generic success");
		}
	}
?>
