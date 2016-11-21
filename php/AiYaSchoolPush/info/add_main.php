<?php

require_once '../db.php';
require_once '../format.php';
require_once '../table.php';

file_get_contents("php://input");

$link = DataBaseUtil::getInstance()->connect();
if ($link->errno != 0){
	Response::json_response(-1,"数据库连接失败",null);
	exit;
}
mysqli_set_charset($link, "utf8"); // 设置编码为utf8


$time = time();
@$classid = $_POST['classId'];
@$username = $_POST['username'];
@$infotype = $_POST['infoType'];
@$content = $_POST['content'];

$query = "insert into ".TABLE_MAIN." (classid,username,time,infotype,content) values('"
		.$classid."','".$username."','".$time."','".$infotype."','".$content."')";
$result = mysqli_query($link, $query);
$num = mysqli_affected_rows($link);
if ($num == 1){
	Response::json_response(0,"提交数据到服务器成功！",null);
}else{
	Response::json_response(-1,"提交数据到服务器失败",null);
}

?>

