<?php
header('Content-Type: application/json');
$ret = new array();
$ret["request"] = _GET['request'];
echo json_encode($ret);
?>
