<?php
header('Content-Type: application/json');
$ret = array(
	"request" => $_GET['request']
);
echo json_encode($ret);
?>
