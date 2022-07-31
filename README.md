# Spring Securityを使ったJWTによる認証サンプル

## 背景
SPA＋APIを使ったアプリケーション構成が一般的になってきたのでCookieeによるセッション維持ではなく、JWTを使ったステートレスな構成なセッション維持が求められる。そのため、JWTを使ってセッションを維持をするサンプルを作った。

## 内容
Spring Securityが提供するFormLoginの処理フローのみを利用している。
Configにおいて、formLogin()することで、/loginのエンドポイントができるのでそれを利用している。
最初のログインは/loginに対して、POSTすることで実現している。
このログインが成功すると、X-JWT-TOKENヘッダ（独自）にJWTトークンが返戻される。
このJWTトークンを使って、AuthrizationヘッダにBearerとして付与したリクエストはSpring
Securityで保護されているエンドポイントにも認証されアクセスすることができる。

## Step
サンプルを作るに当たって、段階を踏んだ。
### Step1
Step1では、JDBCUserDetailManagerを使ってログインと保護されるアクセス(/home)とそうでないアクセス(/)を用意した。
FormでLoginをすることでアクセス可能となる。
準備として、DataSourceの準備と認証情報の準備が必要となる。
この例では、DockerにてMySQLを準備して、ルートパスワードをdemoとしている。
（本番環境ではrootアクセするのはNGだがDocker環境ということで省略した）
スキーマ（データベース）として、demoを作り、以下のSQLを実行した。これはspring Securityによって提供されているものをMySQLに対応したものとなる。

```sql
create table users(
	username varchar(50) not null primary key,
	password varchar(500) not null,
	enabled boolean not null
);

create table authorities (
	username varchar(50) not null,
	authority varchar(50) not null,
	constraint fk_authorities_users foreign key(username) references users(username)
);
create unique index ix_auth_username on authorities (username,authority);

```
ここでPasswordEncoderはデフォルトのものを使っているのでBcryptになります。
ユーzを追加します。最初のパスワードは、固定としてBcryptでエンコードされているものがマニュアルにある（平文はpassword)のでそれを利用して、root/userの二つのユーザを作成しておく。
```sql
insert into users values ('root','{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',true)
insert into users values ('user','{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',true)

insert into authorities values ('root','admin');
insert into authorities values ('root','user');
insert into authorities values ('user','user');
```

/home は認証が必要ですが、認証結果からユーザ名、ロールを返戻します。
以下はFormでroot/passwordとして入力した例。
```json
{
    "name": "Home",
    "value": "1230",
    "userid": "root",
    "roles": "admin,user"
}
```

### Step2
毎回ユーザ作成をSQLで実行するのは大変なので、/users/createエンドポイントでユーザ作成を実現した。
これは Spring　SecurityのUserDetailManagerを使って実装しています。
```java
        UserDetails user = User.withUsername(request.getUserName())
                .password(encoder.encode(request.getPassword()))
                .roles(request.getRoles())
                .build();

        users.createUser(user);
```
としています。このリクエストはJSONによるPOSTリクエストとなる。（/usr/create)
```json
{
    "userName": "test2",
    "password": "qweasd",
    "roles": ["user","admin"]
}
```
この段階ではパスワードは平文、格納する際にPasswordEncoderを使って格納する。

### Step3
SuccessHandler を利用して、JWTを戻す処理を追加。
JJWTを使って。X-JWT-TOKENヘッダ（独自）に戻している。
/loginでBodyにform-dataでusername/passwordをセットしてログインを成功すると
```
eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJyb290IiwiaWF0IjoxNjU5Mjk5Nzk4LCJleHAiOjE2NTkzMDAwOTgsInJvbGVzIjoiYWRtaW4sdXNlciJ9.S9PdwyiZoI0KXBgaCEvRAZYcqsS8XLp5dN89N6lfdEURGmHUjnnRz15BVXMaXDY3f_r18QMMp5Fk6soI5In3ww
```
のように戻る。
共通鍵はjwt.secret.keyに設定している。JWTの署名アルゴリズムはHS512を利用している。

### Step4
JWTが含まれている場合は、認証済としてJWTにあるSubjectとrolesから認証・認可を戻る処理をするFilterを追加。
Step3で実装すべきだったトークンの有効期限（検証して認証済とするため、期限は短い方が良い）を追加した。
有効期間はjwt.secret.expire.millisecondで設定し300秒としている。

セッションが維持されているとトークンが意味をなさないのでステートレスの構成とした。

## 今後、実装する内容
+ Form画面のHTMLが未認証の場合に戻るのでその点を変更
+ RefreshTokenの発行（DBに入れて、検証時にはサブジェクトとトークンの検証をしてトークンを再発行する）



