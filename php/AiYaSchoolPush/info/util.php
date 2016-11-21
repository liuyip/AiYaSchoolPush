<?php

require_once '../table.php';
require_once '../db.php';
require_once '../format.php';

class Util{
	/**
	 * 获取用户是否点赞
	 * @param unknown $username	用户名
	 * @param unknown $mainid	主贴id
	 * @return boolean
	 */
	public static function getPraiseInfo($username,$mainId){
		$link = DataBaseUtil::getInstance()->connect();
		$query = "select * from " . TABLE_PRAISE . " where username = " . $username . " and mainid = " . $mainId;
		mysqli_query($link, $query);
		$num = mysqli_affected_rows($link);
		if ($num == 0){
			return false;
		}
		return true;
	}
	
	/**
	 * 获取主贴评论信息
	 * @param string $mainid	主贴id
	 * @return NULL|unknown[]|NULL
	 */
	public static function getCommentInfo($mainid = ''){
		$link = DataBaseUtil::getInstance()->connect();
		mysqli_set_charset($link, "utf8"); // 设置编码为utf8
		$query = "select * from ".TABLE_COMMENT." where mainid = ".$mainid;
		$result = mysqli_query($link, $query);
		$arr = array();
		$i = 0;
		while (@$row = mysqli_fetch_array($result)){
			$arr[$i]['infoid'] = $row['infoid']; //信息id
			$arr[$i]['mainid'] = $row['mainid']; //条目id
			$arr[$i]['username'] = $row['username'];// 用户名
			$arr[$i]['time'] = $row['time'];
			$arr[$i]['reply'] = $row['reply']; // 回复人id
			$arr[$i]['content'] = $row['content']; // 评论内容
			$arr[$i]['commentUser'] = getUser($row['username']); // 评论人信息
			$arr[$i]['replyUser'] = getUser($row['reply']); // 回复给的人信息
			$i++;
		}
		if ($i == 0){
			return null;
		}
		return $arr;
	}
	
	
	/**
	 * 获取发帖用户信息
	 * @param string $username	用户名
	 * @return unknown[]|NULL	用户信息数组
	 */
	public static function getUser($username = ''){
		$link = DataBaseUtil::getInstance()->connect();
		mysqli_set_charset($link, "utf8"); // 设置编码为utf8
		$query = "select * from ".TABLE_USER." where username = ".$username;
		$result = mysqli_query($link, $query);
		@$row = mysqli_fetch_array($result);
		if ($row){
			$arr = array(
					'username' => $row['username'],
					'nickname' => $row['nickname'],
					'avatar' => $row['avatar']
			);
			return $arr;
		}else{
			return null;
		}
	}
	
	/**
	 * 获取赞或者评论数
	 * @param string $mainid	主贴id
	 * @return unknown			返回数目
	 */
	public static function getInfoCount($mainid,$tableName){
		$link = DataBaseUtil::getInstance()->connect();
		$query = "select * from ".$tableName." where mainid = ".$mainid;
		$result = mysqli_query($link, $query);
		$row = mysqli_affected_rows($link);
		return $row;
	}
	
}