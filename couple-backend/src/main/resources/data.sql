INSERT IGNORE INTO `user` (id, openid, nickname, title, avatar_url, points, contribution, status) VALUES
(1, 'owner-openid', '发炜', '空间管理员', '', 126, 18, 'active'),
(2, 'partner-openid', '轶轶子', '空间管理员', '', 1250, 12, 'active'),
(3, 'family-openid', '家人', '候补成员', '', 34, 3, 'active');

INSERT IGNORE INTO `space` (id, name, type, owner_user_id, announcement, cover_url, current_dress_id, level, status) VALUES
(1, '发炜 & 轶轶的专属空间', 'couple', 1, '此店只为轶轶一人服务', '', 201, 3, 'active');

INSERT IGNORE INTO `space_member` (id, space_id, user_id, role, display_name, status) VALUES
(11, 1, 1, 'owner', '男方', 'active'),
(12, 1, 2, 'admin', '女方', 'active');

INSERT IGNORE INTO `service_category` (id, space_id, name, description, icon_url, sort_order, visible) VALUES
(101, 1, '情绪安抚', '提供满满的情绪价值', '', 1, 1),
(102, 1, '今日投喂', '想吃就点', '', 2, 1),
(103, 1, '跑腿服务', '顺手帮忙', '', 3, 1),
(104, 1, '陪伴服务', '一起消磨时间', '', 4, 1);

INSERT IGNORE INTO `service_item` (id, space_id, category_id, name, description, image_url, tag, point_cost, require_remark, require_appoint_time, daily_limit, cooldown_minutes, monthly_sales, default_assignee_id, sort_order, status) VALUES
(1001, 1, 101, '抱抱 5 分钟', '静静抱一会儿，治愈一整天的疲惫。', '', '情绪安抚', 0, 0, 0, 8, 0, 0, 1, 1, 'active'),
(1002, 1, 101, '疯狂夸夸我', '提供3分钟的情绪价值，全方位无死角赞美。', '', '情绪安抚', 10, 0, 0, 5, 0, 0, 1, 2, 'active'),
(1003, 1, 101, '清空小额购物车', '帮你买下单价不超过50元的心动好物。', '', '情绪安抚', 500, 0, 0, 1, 135, 0, 1, 3, 'active'),
(1004, 1, 102, '买一杯奶茶', '请备注好品牌、糖度和冰度哦。', '', '今日投喂', 50, 1, 0, 2, 0, 0, 1, 4, 'active'),
(1005, 1, 103, '帮忙拿快递', '菜鸟驿站、丰巢都可以，取件码写备注。', '', '跑腿服务', 0, 1, 1, 5, 0, 0, 1, 5, 'active'),
(1006, 1, 104, '陪我散步', '楼下走一圈，顺便聊聊天。', '', '陪伴服务', 0, 0, 1, 2, 60, 0, 1, 6, 'active');

INSERT IGNORE INTO `dress_item` (id, name, type, obtain_type, price_cent, point_price, rarity, status, config_json) VALUES
(201, '淡雅紫主题', 'theme', 'free', 0, 0, 'R', 'active', JSON_OBJECT('background', 'linear-gradient(180deg,#eadef7 0%,#f7f8fa 100%)', 'primaryColor', '#9b88ed', 'cardColor', '#ffffff', 'textColor', '#333333')),
(202, '薄荷汽水', 'theme', 'point', 0, 25, 'SR', 'active', JSON_OBJECT('background', 'linear-gradient(180deg,#effff8 0%,#f8fbff 100%)', 'primaryColor', '#198c7b', 'cardColor', '#f4fffc', 'textColor', '#10201c')),
(203, '夜间电影', 'theme', 'paid', 600, 0, 'SSR', 'active', JSON_OBJECT('background', 'linear-gradient(180deg,#191827 0%,#30283d 46%,#fff 47%)', 'primaryColor', '#f3b35f', 'cardColor', '#fbf4ec', 'textColor', '#201c2a'));

INSERT IGNORE INTO `space_dress` (id, space_id, dress_item_id, obtained_by, obtain_source, active) VALUES
(301, 1, 201, 2, 'default', 1);

INSERT IGNORE INTO `activity` (id, name, type, description, start_date, end_date, reward_point, reward_dress_id, status) VALUES
(401, '七日签到拿装扮', 'checkin', '连续签到 7 天解锁限定背景', DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY), 5, 202, 'active');

INSERT IGNORE INTO `mood_status` (id, space_id, user_id, mood_key, mood_label, note, updated_at) VALUES
(501, 1, 1, 'working', '忙碌中', '晚点集中处理订单', DATE_SUB(NOW(), INTERVAL 20 MINUTE)),
(502, 1, 2, 'tired', '有点累', '需要陪伴', DATE_SUB(NOW(), INTERVAL 5 MINUTE));

INSERT IGNORE INTO `todo_item` (id, space_id, title, description, creator_id, assignee_id, priority, due_time, status) VALUES
(601, 1, '记得提醒对方吃药', '晚上9点感冒药，温开水送服，别忘了！', 2, 1, 3, CONCAT(CURRENT_DATE, ' 21:00:00'), 'pending'),
(602, 1, '周五前交水电气费', '查看微信生活缴费账单，把本月费用清掉。', 2, 2, 2, CONCAT(DATE_ADD(CURRENT_DATE, INTERVAL 2 DAY), ' 23:59:00'), 'pending'),
(603, 1, '买一份早餐', '明早带一份热乎的早餐。', 1, 1, 1, CONCAT(DATE_ADD(CURRENT_DATE, INTERVAL 1 DAY), ' 08:30:00'), 'done');

INSERT IGNORE INTO `wish_item` (id, space_id, creator_id, claimed_by, title, description, type, status) VALUES
(701, 1, 2, NULL, '周末去看电影', '想看新上映的那部', 'date', 'open'),
(702, 1, 2, 1, '想吃火锅', '番茄锅加虾滑', 'food', 'claimed');

INSERT IGNORE INTO `anniversary` (id, space_id, name, date, important) VALUES
(711, 1, '恋爱纪念日', DATE_SUB(CURRENT_DATE, INTERVAL 128 DAY), 1);

INSERT IGNORE INTO `service_order` (id, order_no, space_id, service_item_id, requester_id, assignee_id, status, remark, appoint_time, created_at, completed_at) VALUES
(801, 'SO2026052701', 1, 1004, 2, 1, 'pending', '茶百道，茉莉奶绿，去冰少糖', NULL, '2026-05-27 10:15:00', NULL),
(802, 'SO2026052608', 1, 1005, 2, 1, 'completed', '菜鸟驿站 3 号柜，码 8-2-4015', NULL, '2026-05-26 18:00:00', '2026-05-26 18:30:00');

INSERT IGNORE INTO `message` (id, space_id, receiver_id, sender_id, type, title, content, biz_id, read_status, created_at) VALUES
(901, 1, 1, 2, 'service_order_created', '新的点单来了', '买一杯奶茶：茶百道，茉莉奶绿，去冰少糖', 801, 0, '2026-05-27 10:15:00');

INSERT IGNORE INTO `product` (id, name, type, target_id, amount_cent, status) VALUES
(10001, '夜间电影主题', 'dress', 203, 600, 'active'),
(10002, '空间扩容到 6 人', 'space_capacity', NULL, 900, 'active');

INSERT IGNORE INTO `audit_record` (id, target_type, target_id, status, remark, created_at) VALUES
(11001, 'service_item', 1001, 'pass', '系统初始化菜单已通过', NOW());

INSERT IGNORE INTO `service_template` (id, name, description, scene, sort_order, status) VALUES
(13001, '情侣甜蜜模板', '适合双人点单、陪伴和情绪安抚', 'couple', 1, 'active'),
(13002, '家庭服务模板', '适合多人家庭的家务和点餐', 'family', 2, 'active');

INSERT IGNORE INTO `service_template_item` (id, template_id, category_name, service_name, description, sort_order) VALUES
(13101, 13001, '情绪安抚', '抱抱 5 分钟', '静静抱一会儿，治愈一整天的疲惫。', 1),
(13102, 13001, '今日投喂', '买一杯奶茶', '请备注好品牌、糖度和冰度哦。', 2),
(13103, 13002, '家务分担', '倒垃圾', '晚上出门顺手带走。', 1),
(13104, 13002, '今日投喂', '安排晚饭', '指定今日掌勺人。', 2);

INSERT IGNORE INTO `ui_option` (id, group_key, option_key, label, icon, description, sort_order, status) VALUES
(12001, 'mood', 'sweet', '想被哄', '🥺', '推荐安慰和陪伴', 1, 'active'),
(12002, 'mood', 'happy', '心情很好', '😄', '适合约会和投喂', 2, 'active'),
(12003, 'mood', 'tired', '有点累', '😴', '推荐代办和休息', 3, 'active'),
(12004, 'mood', 'working', '忙碌中', '💻', '减少打扰', 4, 'active'),
(12005, 'mood', 'hungry', '想吃东西', '🍜', '推荐食物服务', 5, 'active'),
(12101, 'quick_signal', 'miss_you', '想你了', '💌', '快来接收一个想念提醒', 1, 'active'),
(12102, 'quick_signal', 'comfort_me', '救救我', '🥺', '需要立刻被安慰一下', 2, 'active'),
(12103, 'quick_signal', 'praise_me', '夸夸我', '✨', '今天需要三句认真夸夸', 3, 'active'),
(12201, 'order_side', 'sent', '我发起的', '', '', 1, 'active'),
(12202, 'order_side', 'received', '我收到的', '', '', 2, 'active'),
(12300, 'order_status', 'all', '全部', '', '', 0, 'active'),
(12301, 'order_status', 'pending', '待接单', '', '', 1, 'active'),
(12302, 'order_status', 'accepted', '已接单', '', '', 2, 'active'),
(12303, 'order_status', 'completed', '已完成', '', '', 3, 'active'),
(12304, 'order_status', 'canceled', '已取消', '', '', 4, 'active'),
(12305, 'order_status', 'rejected', '已拒绝', '', '', 5, 'active'),
(12401, 'todo_status', 'pending', '进行中', '', '', 1, 'active'),
(12402, 'todo_status', 'done', '已完成', '', '', 2, 'active'),
(12501, 'todo_priority', '1', '普通', '', 'priority-normal', 1, 'active'),
(12502, 'todo_priority', '2', '中优先级', '', 'priority-medium', 2, 'active'),
(12503, 'todo_priority', '3', '高优先级', '', 'priority-high', 3, 'active');
