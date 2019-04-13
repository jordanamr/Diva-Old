/*
 Navicat Premium Data Transfer

 Source Server         : Localhost
 Source Server Type    : MariaDB
 Source Server Version : 100138
 Source Host           : localhost:3306
 Source Schema         : diva_game

 Target Server Type    : MariaDB
 Target Server Version : 100138
 File Encoding         : 65001

 Date: 13/04/2019 22:20:22
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for experience_table
-- ----------------------------
DROP TABLE IF EXISTS `experience_table`;
CREATE TABLE `experience_table`  (
  `level` mediumint(7) NOT NULL AUTO_INCREMENT,
  `character` bigint(19) NOT NULL DEFAULT -1,
  `mount` mediumint(7) NOT NULL DEFAULT -1,
  `job` mediumint(7) NOT NULL DEFAULT -1,
  `rank` smallint(5) NOT NULL DEFAULT -1,
  PRIMARY KEY (`level`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 201 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of experience_table
-- ----------------------------
INSERT INTO `experience_table` VALUES (1, 0, 0, 0, 0);
INSERT INTO `experience_table` VALUES (2, 110, 600, 50, 500);
INSERT INTO `experience_table` VALUES (3, 650, 1750, 140, 1500);
INSERT INTO `experience_table` VALUES (4, 1500, 2750, 271, 3000);
INSERT INTO `experience_table` VALUES (5, 2800, 4000, 441, 5000);
INSERT INTO `experience_table` VALUES (6, 4800, 5500, 653, 7500);
INSERT INTO `experience_table` VALUES (7, 7300, 7250, 905, 10000);
INSERT INTO `experience_table` VALUES (8, 10500, 9250, 1199, 12500);
INSERT INTO `experience_table` VALUES (9, 14500, 11500, 1543, 15000);
INSERT INTO `experience_table` VALUES (10, 19200, 14000, 1911, 17500);
INSERT INTO `experience_table` VALUES (11, 25200, 16750, 2330, -1);
INSERT INTO `experience_table` VALUES (12, 32600, 19750, 2792, -1);
INSERT INTO `experience_table` VALUES (13, 41000, 23000, 3297, -1);
INSERT INTO `experience_table` VALUES (14, 50500, 26500, 3840, -1);
INSERT INTO `experience_table` VALUES (15, 61000, 30250, 4439, -1);
INSERT INTO `experience_table` VALUES (16, 75000, 34250, 5078, -1);
INSERT INTO `experience_table` VALUES (17, 91000, 38500, 5762, -1);
INSERT INTO `experience_table` VALUES (18, 115000, 43000, 6493, -1);
INSERT INTO `experience_table` VALUES (19, 142000, 47750, 7280, -1);
INSERT INTO `experience_table` VALUES (20, 171000, 52750, 8097, -1);
INSERT INTO `experience_table` VALUES (21, 202000, 58000, 8980, -1);
INSERT INTO `experience_table` VALUES (22, 235000, 63500, 9898, -1);
INSERT INTO `experience_table` VALUES (23, 270000, 69250, 10875, -1);
INSERT INTO `experience_table` VALUES (24, 310000, 75250, 11903, -1);
INSERT INTO `experience_table` VALUES (25, 353000, 81500, 12985, -1);
INSERT INTO `experience_table` VALUES (26, 398500, 88000, 14122, -1);
INSERT INTO `experience_table` VALUES (27, 448000, 94750, 15315, -1);
INSERT INTO `experience_table` VALUES (28, 503000, 101750, 16564, -1);
INSERT INTO `experience_table` VALUES (29, 561000, 109000, 17873, -1);
INSERT INTO `experience_table` VALUES (30, 621600, 116500, 19242, -1);
INSERT INTO `experience_table` VALUES (31, 687000, 124250, 20672, -1);
INSERT INTO `experience_table` VALUES (32, 755000, 132250, 22166, -1);
INSERT INTO `experience_table` VALUES (33, 829000, 140500, 23726, -1);
INSERT INTO `experience_table` VALUES (34, 910000, 149000, 25353, -1);
INSERT INTO `experience_table` VALUES (35, 1000000, 157750, 27048, -1);
INSERT INTO `experience_table` VALUES (36, 1100000, 166750, 28815, -1);
INSERT INTO `experience_table` VALUES (37, 1240000, 176000, 30656, -1);
INSERT INTO `experience_table` VALUES (38, 1400000, 185500, 32572, -1);
INSERT INTO `experience_table` VALUES (39, 1580000, 195250, 34566, -1);
INSERT INTO `experience_table` VALUES (40, 1780000, 205250, 36641, -1);
INSERT INTO `experience_table` VALUES (41, 2000000, 215500, 38800, -1);
INSERT INTO `experience_table` VALUES (42, 2250000, 226000, 41044, -1);
INSERT INTO `experience_table` VALUES (43, 2530000, 236750, 43378, -1);
INSERT INTO `experience_table` VALUES (44, 2850000, 247750, 45804, -1);
INSERT INTO `experience_table` VALUES (45, 3200000, 249000, 48325, -1);
INSERT INTO `experience_table` VALUES (46, 3570000, 270500, 50946, -1);
INSERT INTO `experience_table` VALUES (47, 3960000, 282250, 53669, -1);
INSERT INTO `experience_table` VALUES (48, 4400000, 294250, 56498, -1);
INSERT INTO `experience_table` VALUES (49, 4860000, 306500, 59437, -1);
INSERT INTO `experience_table` VALUES (50, 5350000, 319000, 62491, -1);
INSERT INTO `experience_table` VALUES (51, 5860000, 331750, 65664, -1);
INSERT INTO `experience_table` VALUES (52, 6390000, 344750, 68960, -1);
INSERT INTO `experience_table` VALUES (53, 6950000, 358000, 72385, -1);
INSERT INTO `experience_table` VALUES (54, 7530000, 371500, 75943, -1);
INSERT INTO `experience_table` VALUES (55, 8130000, 385250, 79640, -1);
INSERT INTO `experience_table` VALUES (56, 8765100, 399250, 83482, -1);
INSERT INTO `experience_table` VALUES (57, 9420000, 413500, 87475, -1);
INSERT INTO `experience_table` VALUES (58, 10150000, 428000, 91624, -1);
INSERT INTO `experience_table` VALUES (59, 10894000, 442750, 95937, -1);
INSERT INTO `experience_table` VALUES (60, 11650000, 457750, 100421, -1);
INSERT INTO `experience_table` VALUES (61, 12450000, 473000, 105082, -1);
INSERT INTO `experience_table` VALUES (62, 13280000, 488500, 109930, -1);
INSERT INTO `experience_table` VALUES (63, 14130000, 504250, 114971, -1);
INSERT INTO `experience_table` VALUES (64, 15170000, 520250, 120215, -1);
INSERT INTO `experience_table` VALUES (65, 16251000, 536500, 125671, -1);
INSERT INTO `experience_table` VALUES (66, 17377000, 553000, 131348, -1);
INSERT INTO `experience_table` VALUES (67, 18553000, 569750, 137256, -1);
INSERT INTO `experience_table` VALUES (68, 19778000, 586750, 143407, -1);
INSERT INTO `experience_table` VALUES (69, 21055000, 604000, 149811, -1);
INSERT INTO `experience_table` VALUES (70, 22385000, 621500, 156481, -1);
INSERT INTO `experience_table` VALUES (71, 23529000, 639250, 163429, -1);
INSERT INTO `experience_table` VALUES (72, 25209000, 657250, 170669, -1);
INSERT INTO `experience_table` VALUES (73, 26707000, 675500, 178214, -1);
INSERT INTO `experience_table` VALUES (74, 28264000, 694000, 186080, -1);
INSERT INTO `experience_table` VALUES (75, 29882000, 712750, 194283, -1);
INSERT INTO `experience_table` VALUES (76, 31563000, 731750, 202839, -1);
INSERT INTO `experience_table` VALUES (77, 33307000, 751000, 211765, -1);
INSERT INTO `experience_table` VALUES (78, 35118000, 770500, 221082, -1);
INSERT INTO `experience_table` VALUES (79, 36997000, 790250, 230808, -1);
INSERT INTO `experience_table` VALUES (80, 38945000, 810250, 240964, -1);
INSERT INTO `experience_table` VALUES (81, 40965000, 830500, 251574, -1);
INSERT INTO `experience_table` VALUES (82, 43059000, 851000, 262660, -1);
INSERT INTO `experience_table` VALUES (83, 45229000, 871750, 274248, -1);
INSERT INTO `experience_table` VALUES (84, 47476000, 892750, 286364, -1);
INSERT INTO `experience_table` VALUES (85, 49803000, 914000, 299037, -1);
INSERT INTO `experience_table` VALUES (86, 52211000, 935500, 312297, -1);
INSERT INTO `experience_table` VALUES (87, 54704000, 957250, 326175, -1);
INSERT INTO `experience_table` VALUES (88, 57284000, 979250, 340705, -1);
INSERT INTO `experience_table` VALUES (89, 59952000, 1001500, 355924, -1);
INSERT INTO `experience_table` VALUES (90, 62712000, 1024000, 371870, -1);
INSERT INTO `experience_table` VALUES (91, 65565000, 1046750, 388582, -1);
INSERT INTO `experience_table` VALUES (92, 68514000, 1069750, 406106, -1);
INSERT INTO `experience_table` VALUES (93, 71561000, 1093000, 424486, -1);
INSERT INTO `experience_table` VALUES (94, 74710000, 1116500, 443772, -1);
INSERT INTO `experience_table` VALUES (95, 77963000, 1140250, 464016, -1);
INSERT INTO `experience_table` VALUES (96, 81323000, 1164250, 485274, -1);
INSERT INTO `experience_table` VALUES (97, 84792000, 1188500, 507604, -1);
INSERT INTO `experience_table` VALUES (98, 88374000, 1213000, 531071, -1);
INSERT INTO `experience_table` VALUES (99, 92071000, 1237750, 555541, -1);
INSERT INTO `experience_table` VALUES (100, 95886000, 1262750, 581687, -1);
INSERT INTO `experience_table` VALUES (101, 99823000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (102, 103885000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (103, 108075000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (104, 112396000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (105, 116853000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (106, 121447000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (107, 126184000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (108, 131066000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (109, 136098000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (110, 141283000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (111, 146626000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (112, 152130000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (113, 157800000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (114, 163640000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (115, 169655000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (116, 175848000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (117, 182225000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (118, 188791000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (119, 195550000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (120, 202507000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (121, 209667000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (122, 217037000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (123, 224620000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (124, 232424000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (125, 240452000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (126, 248712000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (127, 257209000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (128, 265949000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (129, 274939000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (130, 284186000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (131, 293694000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (132, 303473000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (133, 313527000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (134, 323866000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (135, 334495000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (136, 345423000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (137, 356657000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (138, 368206000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (139, 380076000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (140, 392278000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (141, 404818000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (142, 417706000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (143, 430952000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (144, 444564000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (145, 458551000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (146, 472924000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (147, 487693000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (148, 502867000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (149, 518458000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (150, 534476000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (151, 502867000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (152, 567839000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (153, 585206000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (154, 603047000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (155, 621374000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (156, 640199000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (157, 659536000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (158, 679398000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (159, 699798000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (160, 720751000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (161, 742772000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (162, 764374000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (163, 787074000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (164, 810387000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (165, 834329000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (166, 858917000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (167, 884167000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (168, 910098000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (169, 936727000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (170, 964073000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (171, 992154000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (172, 1020991000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (173, 1050603000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (174, 1081010000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (175, 1112235000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (176, 1144298000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (177, 1177222000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (178, 1211030000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (179, 1245745000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (180, 1281393000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (181, 1317997000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (182, 1355584000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (183, 1404179000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (184, 1463811000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (185, 1534506000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (186, 1616294000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (187, 1709205000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (188, 1813267000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (189, 1928513000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (190, 2054975000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (191, 2192686000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (192, 2341679000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (193, 2501990000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (194, 2673655000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (195, 2856710000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (196, 3051194000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (197, 3257146000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (198, 3474606000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (199, 3703616000, -1, -1, -1);
INSERT INTO `experience_table` VALUES (200, 7407232000, -1, -1, -1);

SET FOREIGN_KEY_CHECKS = 1;
