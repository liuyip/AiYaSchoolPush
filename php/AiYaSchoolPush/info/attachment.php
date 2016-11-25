<?php
require_once '../format.php';

$target_path_head = "./pic"; // 接收文件目录

file_get_contents ( "php://input" );

@$size = $_POST ['size'];
$flag = false;
for($i = 0; $i < $size; $i ++) {
	@$filename = $_FILES ['file'.$i] ['name']; // 显示客户端文件的原名称
	@$error = $_FILES ['file'.$i] ['error'];
	$target_path = $target_path_head."/".$filename;
	// $target_path = $filename;
	if (is_uploaded_file ( @$_FILES ['file'.$i] ['tmp_name'] )) {
		$flag = move_uploaded_file ( @$_FILES ['file'.$i] ['tmp_name'], $target_path );
		@$error = @$error.":".@$filename."上传错误";
	} else {
		$flag = false;
	}
}

if ($flag) {
	Response::json_response ( 0, "附件上传成功！", null );
} else {
	Response::json_response ( - 1, "附件上传失败！:" . @$error, null );
}
