-----------------------------------BRANDS------------------------------
INSERT INTO ACTIVITY_TYPE VALUES('A04', 'JOIN', 'N');
INSERT INTO ACTIVITY_TYPE VALUES('A05', 'REDEEM', 'N');


INSERT INTO USERS VALUES('Brand01', '123456', 'BRAND');
INSERT INTO BRAND VALUES('Brand01', 'Brand X', '503 Rolling Creek Dr Austin, AR', TO_DATE('01-APR-2021','DD-MON-YY'));
INSERT INTO TIER VALUES('Bronze', 'Brand01', 1, 0, 1);
INSERT INTO TIER VALUES('Silver', 'Brand01', 2, 170, 2);
INSERT INTO TIER VALUES('Gold', 'Brand01', 3, 270, 3);
INSERT INTO ACTIVITY_TYPE VALUES('A01', 'Purchase', 'Y');
INSERT INTO ACTIVITY_TYPE VALUES('A02', 'Write A Review', 'Y');
INSERT INTO RE_RULES VALUES('RER01', 15, 'A01', 'Brand01', 1);
INSERT INTO RE_RULES VALUES('RER02', 10, 'A02', 'Brand01', 1);
INSERT INTO REWARD_TYPE VALUES('R01', 'Gift Card');
INSERT INTO REWARD_TYPE VALUES('R02', 'Free Product');
INSERT INTO RR_RULES VALUES('RRR01', 80, 'R01', 'Brand01', 1);
INSERT INTO RR_RULES VALUES('RRR02', 70, 'R02', 'Brand01', 1);
INSERT INTO REWARD VALUES('Reward01', 'R01', null, 40, 'Brand01');
INSERT INTO REWARD VALUES('Reward02', 'R02', null, 25, 'Brand01');
INSERT INTO LP_ACT_CATEGORY VALUES('Brand01', 'A01');
INSERT INTO LP_ACT_CATEGORY VALUES('Brand01', 'A02');


INSERT INTO USERS VALUES('Brand02', '123456', 'BRAND');
INSERT INTO BRAND VALUES('Brand02', 'Brand Y', '939 Orange Ave Coronado, CA', TO_DATE('25-MAR-2021','DD-MON-YY'));
INSERT INTO TIER VALUES('Special', 'Brand02', 1, 0, 1);
INSERT INTO TIER VALUES('Premium', 'Brand02', 2, 210, 2);
INSERT INTO ACTIVITY_TYPE VALUES('A03', 'Refer A Friend' , 'Y');
INSERT INTO RE_RULES VALUES('RER03', 40, 'A01', 'Brand02', 1);
INSERT INTO RE_RULES VALUES('RER04', 30, 'A03', 'Brand02', 1);
INSERT INTO RR_RULES VALUES('RRR03', 120, 'R01', 'Brand02', 1);
INSERT INTO RR_RULES VALUES('RRR04', 90, 'R02', 'Brand02', 1);
INSERT INTO REWARD VALUES('Reward03', 'R01', null, 30, 'Brand02');
INSERT INTO REWARD VALUES('Reward04', 'R02', null, 50, 'Brand02');
INSERT INTO LP_ACT_CATEGORY VALUES('Brand02', 'A01');
INSERT INTO LP_ACT_CATEGORY VALUES('Brand02', 'A03');


INSERT INTO USERS VALUES('Brand03', '123456', 'BRAND');
INSERT INTO BRAND VALUES('Brand03', 'Brand Z', '20 Roszel Rd Princeton, NJ', TO_DATE('08-MAY-2021','DD-MON-YY'));
INSERT INTO RE_RULES VALUES('RER05', 10, 'A03', 'Brand03', 1);
INSERT INTO RR_RULES VALUES('RRR05',100, 'R01', 'Brand03', 1);
INSERT INTO REWARD VALUES('Reward05', 'R01', null, 25, 'Brand03');
INSERT INTO LP_ACT_CATEGORY VALUES('Brand03', 'A03');


-----------------------------------CUSTOMERS------------------------------
INSERT INTO USERS VALUES('C0001', '123456', 'CUSTOMER');
INSERT INTO CUSTOMER VALUES('C0001', 'Peter Parker', '8636368778', '20 Ingram Street, NY');
INSERT INTO USERS VALUES('C0002', '123456', 'CUSTOMER');
INSERT INTO CUSTOMER VALUES('C0002', 'Steve Rogers', '8972468552', '569 Leaman Place, NY');
INSERT INTO USERS VALUES('C0003', '123456', 'CUSTOMER');
INSERT INTO CUSTOMER VALUES('C0003', 'Diana Prince', '8547963210', '1700 Broadway St, NY');
INSERT INTO USERS VALUES('C0004', '123456', 'CUSTOMER');
INSERT INTO CUSTOMER VALUES('C0004', 'Billy Batson', '8974562583', '5015 Broad St, Philadelphia, PA');
INSERT INTO USERS VALUES('C0005', '123456', 'CUSTOMER');
INSERT INTO CUSTOMER VALUES('C0005', 'Tony Stark', '8731596464', '10880 Malibu Point, CA');

INSERT INTO WALLET VALUES (6020, 'Brand01', 'C0001', 0, 'Bronze', 80);
INSERT INTO WALLET VALUES (6030, 'Brand02', 'C0001', 0, 'Premium', 210);
INSERT INTO WALLET VALUES (6040, 'Brand01', 'C0002', 0, 'Bronze', 70);
INSERT INTO WALLET VALUES (6050, 'Brand03', 'C0003', 40, null, null);
INSERT INTO WALLET VALUES (6051, 'Brand02', 'C0003', 40, 'Premium', 220);
INSERT INTO WALLET VALUES (6060, 'Brand01', 'C0005', 20, 'Silver', 170);
INSERT INTO WALLET VALUES (6061, 'Brand02', 'C0005', 40, 'Special', 160);
INSERT INTO WALLET VALUES (6070, 'Brand03', 'C0005', 50, null, null);

INSERT INTO WALLET_GIFTCARD VALUES(6020);
INSERT INTO WALLET_GIFTCARD VALUES(6030);
INSERT INTO WALLET_GIFTCARD VALUES(6060);
INSERT INTO WALLET_GIFTCARD VALUES(6061);

UPDATE REWARD SET QUANTITY=QUANTITY-2 WHERE BRAND_ID='Brand01' AND REWARD_CATEGORY_CODE='R01';
UPDATE REWARD SET QUANTITY=QUANTITY-2 WHERE BRAND_ID='Brand02' AND REWARD_CATEGORY_CODE='R01';
UPDATE REWARD SET QUANTITY=QUANTITY-3 WHERE BRAND_ID='Brand02' AND REWARD_CATEGORY_CODE='R02';
UPDATE REWARD SET QUANTITY=QUANTITY-2 WHERE BRAND_ID='Brand01' AND REWARD_CATEGORY_CODE='R02';

