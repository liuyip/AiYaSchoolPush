<?php
require_once '../db.php';
require_once '../format.php';
require_once '../table.php';
require_once 'util.php';

file_get_contents ( "php://input" );

$link = DataBaseUtil::getInstance ()->connect ();
if ($link->errno != 0) {
	Response::json_response ( - 1, "数据库连接失败", null );
	exit ();
}
mysqli_set_charset ( $link, "utf8" ); // 设置编码为utf8

$time = time ();
@$classid = $_POST ['classId'];
@$username = $_POST ['username'];
@$infotype = $_POST ['infoType'];
@$content = $_POST ['content'];
@$picCount = $_POST ['picCount'];

$query = "insert into " . TABLE_MAIN . " (classid,username,time,infotype,content) values('" . $classid . "','" . $username . "','" . $time . "','" . $infotype . "','" . $content . "')";
mysqli_query ( $link, $query );
$num = mysqli_affected_rows ( $link );

$mainid = Util::getMainId ( $username, $time, $infotype );
for($i = 0; $i < $picCount; $i ++) {
	Util::insertPicUrls ( $mainid, $_POST ['picUrl' . $i] );
}

if ($num == 1) {
	Response::json_response ( 0, "提交数据到服务器成功！", getMainInfo ( $username, $time, $infotype ) );
} else {
	Response::json_response ( - 1, "提交数据到服务器失败", null );
}

/**
 * 返回发布的信息情况
 */
function getMainInfo($username, $time, $infotype) {
	$link = DataBaseUtil::getInstance ()->connect ();
	mysqli_set_charset ( $link, "utf8" ); // 设置编码为utf8
	$query = "select * from " . TABLE_MAIN . " where username = " . $username . " and time = " . $time . " and infotype = " . $infotype;
	$result = mysqli_query ( $link, $query );
	$num = mysqli_affected_rows ( $link );
	if ($num == 0) {
		return null;
	}
	@$row = mysqli_fetch_array ( $result );
	$arr = array (
			'mainid' => $row ['mainid'],
			'classid' => $row ['classid'],
			// 'username'=>$row['username'],
			'time' => $row ['time'],
			'infotype' => $row ['infotype'],
			'content' => $row ['content'],
			'user' => getUserInfo ( $row ['username'] ),
			'picUrls' => Util::getPicInfo ( $row ['mainid'] ) 
	);
	return $arr;
}

/**
 * 获取发布人的信息的情况
 */
function getUserInfo($username) {
	$link = DataBaseUtil::getInstance ()->connect ();
	mysqli_set_charset ( $link, "utf8" ); // 设置编码为utf8
	$query = "select * from " . TABLE_USER . " where username = " . $username;
	$result = mysqli_query ( $link, $query );
	@$row = mysqli_fetch_array ( $result );
	$num = mysqli_affected_rows ( $link );
	if ($num == 0) {
		return null;
	}
	return array (
			'username' => $row ['username'],
			'nickname' => $row ['nickname'],
			'avatar' => $row ['avatar'] 
	);
}

?>

