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
BEGIN -- will this work if no re rule exist;
    SELECT MAX(VERSION_NO) INTO CURRVNO FROM RE_RULES WHERE BRAND_ID = bId AND ACT_CATEGORY_CODE = acCode;
    SELECT COUNT(RER_CODE) INTO EXISTINGCNT FROM RE_RULES WHERE RER_CODE = rerCode;
    IF CURRVNO > 0 THEN
        -- Insert into rerules table
        INSERT INTO RE_RULES(RER_CODE, BRAND_ID, ACT_CATEGORY_CODE, POINTS, VERSION_NO) values (rerCode, bId, acCode, pts, CURRVNO + 1);
        ret := 1;
    ELSIF EXISTINGCNT = 0 THEN
        ret := 2;
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

    IF SAMERULECOUNT > 0 THEN
        ret := 0;
    ELSIF REWTYPECOUNT = 0 THEN
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
CURRVNO INT;
EXISTINGCNT INT;
BEGIN -- will this work if no rr rule exist;
    SELECT MAX(VERSION_NO) INTO CURRVNO FROM RR_RULES WHERE BRAND_ID = bId AND REWARD_CATEGORY_CODE = acCode;
    SELECT COUNT(RRR_CODE) INTO EXISTINGCNT FROM RR_RULES WHERE RRR_CODE = rrrCode;
    IF CURRVNO > 0 THEN
        -- Insert into rrrules table
        INSERT INTO RR_RULES(RRR_CODE, BRAND_ID, REWARD_CATEGORY_CODE, POINTS, VERSION_NO) values (rrrCode, bId, rcCode, pts, CURRVNO + 1);
        ret := 1;
    ELSIF EXISTINGCNT = 0 THEN
        ret := 2;
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
        UPDATE LOYALTY_PROGRAM SET STATE = "ACTIVE" WHERE BRAND_LP_ID = bId;
        ret := 3;
    END IF;
END;
/