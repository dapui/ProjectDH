### INFO
```
{
	"client":"***",
	"domain":"***",
	"clientId":"***",
	"clientSecret":"***",
	"serviceAccount":"***",
	"privateKey":"***",
	"oauthScopes":"***",
	"approvalBotId":"***",
	"birthdayBotId":"***",
	"boardBotId":"***",
	"calendarBotId":"***",
	"domainId":"***",
}
```


### DB sql
```
DROP TABLE IF EXISTS tbl_user_naverworks;
CREATE TABLE tbl_user_naverworks(
    USERID varchar(80) PRIMARY KEY,
    CN varchar(80),
    USERNAME varchar(80),
    DOMAINID varchar(80),
    MAIL varchar(100),
    ACCESSTOKEN varchar(600),
    REFRESHTOKEN varchar(600),
    CREATEDATE datetime default now(),
    EXPIRATIONDATE datetime
); 
```

