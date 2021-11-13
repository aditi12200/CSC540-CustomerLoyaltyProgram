-----------------------------------Creating Tables------------------------------
CREATE TABLE USERS(USER_ID VARCHAR2(20) PRIMARY KEY, PWD VARCHAR2(30), TYPE VARCHAR2(20));

CREATE TABLE CUSTOMER(CUST_ID VARCHAR2(20) PRIMARY KEY, NAME VARCHAR2(20), PHONENO CHAR(10), ADDRESS VARCHAR2(50), FOREIGN KEY(CUST_ID) REFERENCES USERS(USER_ID));

CREATE TABLE BRAND(BRAND_ID VARCHAR2(20) PRIMARY KEY, NAME VARCHAR2(30), ADDRESS VARCHAR(50), JOIN_DATE DATE, FOREIGN KEY(BRAND_ID) REFERENCES USERS(USER_ID));

CREATE TABLE LOYALTY_PROGRAM(BRAND_LP_ID VARCHAR2(20), TYPE VARCHAR2(20), STATE VARCHAR(20), FOREIGN KEY(BRAND_LP_ID) REFERENCES BRAND(BRAND_ID));

CREATE TABLE WALLET_ACTIVITY(WALLET_ID VARCHAR2(20), ACT_ID INTEGER, PRIMARY KEY(WALLET_ID, ACT_ID), FOREIGN KEY(WALLET_ID) REFERENCES WALLET(WALLET_ID), FOREIGN KEY(ACT_ID) REFERENCES ACTIVITY(ACT_ID));

CREATE TABLE REWARD_TYPE(RT_ID VARCHAR2(10) PRIMARY KEY, REWARD_NAME VARCHAR(30) NOT NULL);

CREATE TABLE RR_RULES(RRR_CODE VARCHAR2(6), POINTS NUMBER NOT NULL, REWARD_CATEGORY_CODE VARCHAR2(10), BRAND_ID VARCHAR2(20), VERSION_NO NUMBER, PRIMARY KEY(RRR_CODE), FOREIGN KEY(BRAND_ID) REFERENCES BRAND(BRAND_ID), FOREIGN KEY(REWARD_CATEGORY_CODE) REFERENCES REWARD_TYPE(RT_ID));

CREATE TABLE ACTIVITY_TYPE(AT_ID VARCHAR2(10) PRIMARY KEY, ACTIVITY_NAME VARCHAR(30) NOT NULL, VISIBLE CHAR(1));

CREATE TABLE TIER(TIER_NAME VARCHAR(10), BRAND_ID VARCHAR2(20), PRECEDENCE INTEGER, POINTS INTEGER NOT NULL, MULTIPLIER INTEGER NOT NULL, PRIMARY KEY (TIER_NAME, BRAND_ID), FOREIGN KEY(BRAND_ID) REFERENCES BRAND(BRAND_ID));

CREATE TABLE RE_RULES(RER_CODE VARCHAR2(6), POINTS INTEGER, ACT_CATEGORY_CODE VARCHAR2(10), BRAND_ID VARCHAR2(20), VERSION_NO NUMBER, PRIMARY KEY(BRAND_ID,VERSION_NO), FOREIGN KEY(BRAND_ID) REFERENCES BRAND(BRAND_ID), FOREIGN KEY(ACT_CATEGORY_CODE) REFERENCES ACTIVITY_TYPE(AT_ID));

CREATE TABLE LP_ACT_CATEGORY(BRAND_ID VARCHAR2(20), ACT_CATEGORY_CODE VARCHAR2(10), PRIMARY KEY(BRAND_ID,ACT_CATEGORY_CODE),FOREIGN KEY(BRAND_ID) REFERENCES BRAND(BRAND_ID), FOREIGN KEY(ACT_CATEGORY_CODE) REFERENCES ACTIVITY_TYPE(AT_ID));

CREATE TABLE WALLET(WALLET_ID VARCHAR2(20) NOT NULL UNIQUE, BRAND_ID VARCHAR2(20), CUST_ID VARCHAR2(20), POINTS INTEGER, TIER_STATUS VARCHAR(10), CUMULATIVE_PTS INTEGER, PRIMARY KEY(BRAND_ID, CUST_ID), FOREIGN KEY(BRAND_ID) REFERENCES BRAND(BRAND_ID), FOREIGN KEY(CUST_ID) REFERENCES CUSTOMER(CUST_ID));
CREATE SEQUENCE S_WALLET START WITH 6000 INCREMENT BY 1 CACHE 10;
CREATE OR REPLACE TRIGGER T_WALLET BEFORE INSERT ON WALLET REFERENCING NEW AS NEW FOR EACH ROW BEGIN
  if(:new.WALLET_ID is null) then SELECT S_WALLET.nextval INTO :new.WALLET_ID FROM dual; end if; END; /

CREATE TABLE ACTIVITY(ACT_ID INTEGER NOT NULL, ACT_DATE DATE, ACT_CATEGORY_CODE VARCHAR2(10), VALUE VARCHAR2(40), PRIMARY KEY(ACT_ID), FOREIGN KEY(ACT_CATEGORY_CODE) REFERENCES ACTIVITY_TYPE(AT_ID));
CREATE SEQUENCE S_ACT START WITH 100 INCREMENT BY 1 CACHE 10;
CREATE OR REPLACE TRIGGER T_ACT_ID BEFORE INSERT ON ACTIVITY REFERENCING NEW AS NEW FOR EACH ROW BEGIN
  if(:new.ACT_ID is null) then SELECT S_ACT.nextval INTO :new.ACT_ID FROM dual; end if; END; /

CREATE TABLE REWARD(REWARD_ID VARCHAR2(20), REWARD_CATEGORY_CODE VARCHAR2(10), VALUE NUMBER, QUANTITY NUMBER NOT NULL, BRAND_ID VARCHAR2(20), PRIMARY KEY(REWARD_ID), FOREIGN KEY(BRAND_ID) REFERENCES BRAND(BRAND_ID), FOREIGN KEY(REWARD_CATEGORY_CODE) REFERENCES REWARD_TYPE(RT_ID));
CREATE SEQUENCE S_REWARD START WITH 1000 INCREMENT BY 1 CACHE 10;
CREATE OR REPLACE TRIGGER T_REWARD_ID BEFORE INSERT ON REWARD REFERENCING NEW AS NEW FOR EACH ROW BEGIN
  if(:new.REWARD_ID is null) then SELECT S_REWARD.nextval INTO :new.REWARD_ID FROM dual; end if; END; /

CREATE TABLE WALLET_GIFTCARD(WALLET_ID VARCHAR2(20), GIFT_CARD_CODE VARCHAR2(20), EXPIRY_DATE DATE, PRIMARY KEY(GIFT_CARD_CODE), FOREIGN KEY(WALLET_ID) REFERENCES WALLET(WALLET_ID));
CREATE SEQUENCE S_GIFTCARD START WITH 67347 INCREMENT BY 1 CACHE 10;
CREATE OR REPLACE TRIGGER T_GIFTCARD BEFORE INSERT ON WALLET_GIFTCARD REFERENCING NEW AS NEW FOR EACH ROW BEGIN
  if(:new.GIFT_CARD_CODE is null) then SELECT S_GIFTCARD.nextval INTO :new.GIFT_CARD_CODE FROM dual; end if; END; /
-----------------------------------Stored Procedures-----------------------------------------------

-- Brand addition by admin
CREATE or REPLACE PROCEDURE admin_add_brand
(
    brandId IN VARCHAR2,
    brandName IN VARCHAR2,
    brandAddress IN VARCHAR2,
    ret OUT INT
)
AS
EXISTINGUSERCNT INT;
JOIN_DATE DATE;
BEGIN
    SELECT COUNT(USER_ID) INTO EXISTINGUSERCNT FROM USERS WHERE USER_ID = brandId;

    IF EXISTINGUSERCNT > 0 THEN
        ret := 0;
    ELSE
        SELECT CURRENT_DATE INTO JOIN_DATE FROM DUAL;
        -- Insert into users table
        INSERT INTO USERS(USER_ID, PWD, TYPE) VALUES(brandId, '123456', 'BRAND');
        -- Insert into brand table
        INSERT INTO BRAND(BRAND_ID, NAME, ADDRESS, JOIN_DATE) VALUES(brandId, brandName, brandAddress, JOIN_DATE);

        ret := 1;
    END IF;
END;
/

-- Customer addition by admin
CREATE or REPLACE PROCEDURE admin_add_customer
(
    customerId IN VARCHAR2,
    customerName IN VARCHAR2,
    customerAddr IN VARCHAR2,
    customerPhone IN VARCHAR2,
    ret OUT INT
)
AS
EXISTINGUSERCNT INT;
BEGIN
    SELECT COUNT(USER_ID) INTO EXISTINGUSERCNT FROM USERS WHERE USER_ID = customerId;
    IF EXISTINGUSERCNT > 0 THEN
        ret := 0;
    ELSE
        -- Insert into users table
        INSERT INTO USERS(USER_ID, PWD, TYPE) VALUES(customerId, '123456', 'CUSTOMER');
        -- Insert into customer table
        INSERT INTO CUSTOMER(CUST_ID, NAME, PHONENO, ADDRESS) VALUES(customerId, customerName, customerPhone, customerAddr);

        ret := 1;
    END IF;
END;
/

-- Brand signup
CREATE or REPLACE PROCEDURE add_brand
(
    brandId IN VARCHAR2,
    brandPassword IN VARCHAR2,
    brandName IN VARCHAR2,
    brandAddress IN VARCHAR2,
    ret OUT INT
)
AS
EXISTINGUSERCNT INT;
JOIN_DATE DATE;
BEGIN
    SELECT COUNT(USER_ID) INTO EXISTINGUSERCNT FROM USERS WHERE USER_ID = brandId;

    IF EXISTINGUSERCNT > 0 THEN
        ret := 0;
    ELSE
        SELECT CURRENT_DATE INTO JOIN_DATE FROM DUAL;
        -- Insert into users table
        INSERT INTO USERS(USER_ID, PWD, TYPE) VALUES(brandId, brandPassword, 'BRAND');
        -- Insert into brand table
        INSERT INTO BRAND(BRAND_ID, NAME, ADDRESS, JOIN_DATE) VALUES(brandId, brandName, brandAddress, JOIN_DATE);

        ret := 1;
    END IF;
END;
/

-- Customer signup
CREATE or REPLACE PROCEDURE add_customer
(
    customerId IN VARCHAR2,
    customerPassword IN VARCHAR2,
    customerName IN VARCHAR2,
    customerAddr IN VARCHAR2,
    customerPhone IN VARCHAR2,
    ret OUT INT
)
AS
EXISTINGUSERCNT INT;
BEGIN
    SELECT COUNT(USER_ID) INTO EXISTINGUSERCNT FROM USERS WHERE USER_ID = customerId;
    IF EXISTINGUSERCNT > 0 THEN
        ret := 0;
    ELSE
        -- Insert into users table
        INSERT INTO USERS(USER_ID, PWD, TYPE) VALUES(customerId, customerPassword, 'CUSTOMER');
        -- Insert into customer table
        INSERT INTO CUSTOMER(CUST_ID, NAME, PHONENO, ADDRESS) VALUES(customerId, customerName, customerPhone, customerAddr);

        ret := 1;
    END IF;
END;
/

-- Adding reward earning rule
create or replace PROCEDURE add_re_rule
(
    bId IN VARCHAR2,
    rerCode IN VARCHAR2,
    acCode IN VARCHAR2,
    pts IN NUMBER,
    ret OUT INT
)
AS
SAMERULECNT INT;
ACTYPECNT INT;
BEGIN
    SELECT COUNT(BRAND_ID) INTO ACTYPECNT FROM LP_ACT_CATEGORY WHERE BRAND_ID = bId AND ACT_CATEGORY_CODE = acCode;
    SELECT COUNT(BRAND_ID) INTO SAMERULECNT FROM RE_RULES WHERE BRAND_ID = bId AND ACT_CATEGORY_CODE = acCode;

    IF SAMERULECNT > 0 THEN
        ret := 0;
    ELSIF ACTYPECNT = 0 THEN
        ret := 2;
    ELSE
        -- Insert into rerules table
        INSERT INTO RE_RULES(RER_CODE, BRAND_ID, ACT_CATEGORY_CODE, POINTS, VERSION_NO) values (rerCode, bId, acCode, pts, 1);
        ret := 1;
    END IF;
END;
/

-- Updating reward earning rule
create or replace PROCEDURE update_re_rule
(
    bId IN VARCHAR2,
    rerCode IN VARCHAR2,
    acCode IN VARCHAR2,
    pts IN NUMBER,
    ret OUT INT
)
AS
CURRVNO INT;
EXISTINGCNT INT;
ACCLPCNT INT;
BEGIN -- will this work if no re rule exist;
    SELECT COUNT(ACT_CATEGORY_CODE) INTO ACCLPCNT FROM LP_ACT_CATEGORY WHERE BRAND_ID = bId AND ACT_CATEGORY_CODE = acCode;
    SELECT MAX(VERSION_NO) INTO CURRVNO FROM RE_RULES WHERE BRAND_ID = bId AND ACT_CATEGORY_CODE = acCode AND RER_CODE=rerCode;
    SELECT COUNT(RER_CODE) INTO EXISTINGCNT FROM RE_RULES WHERE RER_CODE = rerCode;
    IF ACCLPCNT = 0 THEN
        ret := 3;
    ELSIF EXISTINGCNT = 0 THEN
        ret := 2;
    ELSIF CURRVNO > 0 THEN
        -- Insert into rerules table
        INSERT INTO RE_RULES(RER_CODE, BRAND_ID, ACT_CATEGORY_CODE, POINTS, VERSION_NO) values (rerCode, bId, acCode, pts, CURRVNO + 1);
        ret := 1;
    ELSE
        ret := 0;
    END IF;
END;
/

-- Adding reward redeeming rule
create or replace PROCEDURE add_rr_rule
(
    bId IN VARCHAR2,
    rrrCode IN VARCHAR2,
    rcCode IN VARCHAR2,
    pts IN NUMBER,
    ret OUT INT
)
AS
SAMERULECNT INT;
REWTYPECNT INT;
BEGIN
    SELECT COUNT(BRAND_ID) INTO REWTYPECNT FROM REWARD WHERE BRAND_ID = bId AND REWARD_CATEGORY_CODE = rcCode;
    SELECT COUNT(BRAND_ID) INTO SAMERULECNT FROM RR_RULES WHERE BRAND_ID = bId AND REWARD_CATEGORY_CODE = rcCode;

    IF SAMERULECNT > 0 THEN
        ret := 0;
    ELSIF REWTYPECNT = 0 THEN
        ret := 2;
    ELSE
        -- Insert into rrrules table
        INSERT INTO RR_RULES(RRR_CODE, BRAND_ID, REWARD_CATEGORY_CODE, POINTS, VERSION_NO) values (rrrCode, bId, rcCode, pts, 1);
        ret := 1;
    END IF;
END;
/

-- Updating reward redeeming rule
create or replace PROCEDURE update_rr_rule
(
    bId IN VARCHAR2,
    rrrCode IN VARCHAR2,
    rcCode IN VARCHAR2,
    pts IN NUMBER,
    ret OUT INT
)
AS
RCCLPCNT INT;
CURRVNO INT;
EXISTINGCNT INT;
BEGIN -- will this work if no rr rule exist;
    SELECT COUNT(REWARD_CATEGORY_CODE) INTO RCCLPCNT FROM REWARD WHERE BRAND_ID = bId AND REWARD_CATEGORY_CODE = rcCode;
    SELECT MAX(VERSION_NO) INTO CURRVNO FROM RR_RULES WHERE BRAND_ID = bId AND REWARD_CATEGORY_CODE = rcCode AND RRR_CODE = rrrCode;
    SELECT COUNT(RRR_CODE) INTO EXISTINGCNT FROM RR_RULES WHERE RRR_CODE = rrrCode;
    IF RCCLPCNT = 0 THEN
        ret := 3;
    ELSIF EXISTINGCNT = 0 THEN
        ret := 2;
    ELSIF CURRVNO > 0 THEN
        -- Insert into rrrules table
        INSERT INTO RR_RULES(RRR_CODE, BRAND_ID, REWARD_CATEGORY_CODE, POINTS, VERSION_NO) values (rrrCode, bId, rcCode, pts, CURRVNO + 1);
        ret := 1;
    ELSE
        ret := 0;
    END IF;
END;
/

-- Validate loyalty program
create or replace PROCEDURE validate_loyalty_program
(
    bId IN VARCHAR2,
    lpType IN VARCHAR2,
    ret OUT INT
)
AS
RERULECOUNT INT;
RRRULECOUNT INT;
TIERCOUNT INT;
BEGIN
    SELECT COUNT(DISTINCT RER_CODE) INTO RERULECOUNT FROM RE_RULES WHERE BRAND_ID = bId;
    SELECT COUNT(DISTINCT RRR_CODE) INTO RRRULECOUNT FROM RR_RULES WHERE BRAND_ID = bId;

    IF lpType = 'T' THEN
        SELECT COUNT(DISTINCT TIER_NAME) INTO TIERCOUNT FROM TIER WHERE BRAND_ID = bId;
    END IF;

    IF lpType = 'T' AND TIERCOUNT < 1 THEN
        ret := 0;
    ELSIF RERULECOUNT < 1 THEN
        ret := 1;
    ELSIF RRRULECOUNT < 1 THEN
        ret := 2;
    ELSE
        UPDATE LOYALTY_PROGRAM SET STATE = 'ACTIVE' WHERE BRAND_LP_ID = bId;
        ret := 3;
    END IF;
END;
/