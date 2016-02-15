<?php

	/*

		RequestHandler
		For all your needs involving managing requests
	
		Public interface:
		registerDir($path, $dname): registers a directory with name "dname", DOES NOTHING on invalid path
			Shorthand: D()
		registerFunc($path, $fname, $fval): registers a function with name "fname" and value "fval", also does nothing on invalid path
			Shorthand: F()
	
		call($path): call a function, THROWS AN EXCEPTION on invalid path

		Basic use:

		Say I want to register two functions: "foo/bar/mhs" and "foo/mhs":
		$RH = new $RequestHandler(); //create a new request handler
		$RH->D("", "foo"); //register the dir "foo" at root
		$RH->F("foo", "mhs", function() {...}); //register "foo/mhs"
		$RH->D("foo", "bar");
		$RH->F("foo/bar", "mhs"); //register "foo/bar/mhs"

		Wildcards:

		Allow for "catch-alls". For example:

		$WC = $RH->getWildcard();
		$RH->D("foo", $WC); 
		$RH->F("foo/$WC", "mhs", function(){...}); //Will this call "foo/xxx/mhs", where xxx can be anything

		The request handler will pass every function a $trace variable, which is an array of the requests, useful for handling wildcard directories

		Request: foo/aaa/func -> Trace [0 => "foo", 1 = "aaa", 2 = "func"]

		$RH->F("foo/$WC", "bar", function($trace) {
			//$trace[1] to get $WC in the original request
		});

		//Note that registered functions has priority

		$RH->F("foo", $WC, function($val){ }) //Will allow for "foo/xxx", xxx is passed into your function specified


	*/

	class RequestHandler
	{
		private $requestChoice;
		private $wildcard = "*";

		//Specify the way functions are stored
		private function dirEnc($dirname) {
			return "D_".$dirname;
		}
		private function funcEnc($fname) {
			return "F_".$fname;
		}

		//Gets the current directory from a path, returns a ref to $requestChoice
		private function &curDir($path) {
			if(empty($path))
				return $this->requestChoice;

			$ptoken = explode("/", $path);

			$cd = &$this->requestChoice;
			for($i = 0; $i < count($ptoken); ++$i) {

				$dc = $this->dirEnc($ptoken[$i]);
				$dw = $this->dirEnc($this->wildcard);

				//Checks if there is a directory matching OR wildcard
				if(isset($cd[$dc])) {
					$cd = &$cd[$dc];
				} else if(isset($cd[$dw])) {
					$cd = &$cd[$dw];
				} else {
					$null = NULL;
					return $null;
				}
			}
			return $cd;
		}

		public function __construct() {
			$this->requestChoice = array();
		}

		public function registerDir($path, $dname) {
			$cd = &$this->curDir($path);
			if(!is_null($cd)) {
				$cd[$this->dirEnc($dname)] = array();
			}
		}

		public function registerFunc($path, $fname, $fval) {
			$cd = &$this->curDir($path);
			if(!is_null($cd)) {
				$cd[$this->funcEnc($fname)] = $fval;
			}
		}

		public function getWildcard() {
			return $this->wildcard;
		}

		//Shorthand
		public function D($path, $dname) {
			$this->registerDir($path, $dname);
		}
		public function F($path, $fname, $fval) {
			$this->registerFunc($path, $fname, $fval);
		}

		//Calls a function
		//THROWS EXCEPTION on invalid path
		public function call($path) {
			$ptoken = explode("/", $path);
			$trace = $ptoken;
			$fname = end($ptoken);
			array_pop($ptoken);
			$dpath = implode("/", $ptoken);
			$cd = $this->curDir($dpath);
			if(is_null($cd)) {
				throw new Exception("Path Error!");
			}

			$f_cur = $this->funcEnc($fname);
			$f_wc = $this->funcEnc($this->wildcard);

			if(isset($cd[$f_cur]) && is_callable($cd[$f_cur])) {
				return call_user_func($cd[$f_cur], $trace);
			} else if(isset($cd[$f_wc]) && is_callable($cd[$f_wc])) {
				return call_user_func($cd[$f_wc], $trace);
			} else {
				throw new Exception("Path Error!");
			}
			return;
		}
	}

?>