.. _configuration:

=============
Configuration
=============

Configuration Options
---------------------

  mms.admin.username
    The root admin username to use.

  mms.admin.password
    The root admin password to use.

  mms.stream.batch.size
    The limit to use when processing stream data.

    | `Default: 5000`

  rdb.project.prefix
    The prefix to use for project database creation.

    | `Default: ''`

Spring Data Configuration
-------------------------

The following are a list of options to configure the RDB Module for MMS.

  spring.datasource.url
    The datasource url in jdbc format. Required.

  spring.datasource.database
    The database name to use for global MMS configuration. Required.

  spring.datasource.username
    The username to use for authentication. Optional.

  spring.datasource.password
    The password to use for authentication. Optional.

  spring.datasource.driver-class-name
    The driver to use for JDBC. Any database driver supported by Spring Data can be used. Required.

  spring.datasource.initialization-mode
    The initialization mode to use when starting the MMS application. Accepted values are `always`, `embedded`, and `never`. Required.

  spring.jpa.properties.hibernate.dialect
    The hibernate dialect to use. Required.

  spring.jpa.properties.hibernate.dialect.storage_engine
    The storage engine to use. Optional.

  spring.jpa.hibernate.ddl-auto
    The DDL generation option. Accepted values are `none`, `create`, `create-drop`, `validate`, and `update` Required.

Elasticsearch Configuration
---------------------------

The following are a list of options to configure the Elastic Module for MMS.

  elasticsearch.host
    The host name of the Elasticsearch server or cluster. Required.

  elasticsearch.port
    The port number of the Elasticsearch server or cluster. Required.

  elasticsearch.http
    The transport protocol to use to connect to the Elasticsearch server or cluster. Required.

  elasticsearch.limit.result
    The maximum number of results a single search request should return. Optional.

    | `Default: 10000`

  elasticsearch.limit.term
    The maximum number of terms that a search query should contain. Optional.

    | `Default: 1000`

  elasticsearch.limit.scrollTimeout
    The maximum time to wait for search requests. Optional.

    | `Default: 1000`

  elasticsearch.limit.get
    The maximum number of elements that a single get request should return. Optional.

    | `Default: 100000`

  elasticsearch.limit.index
    The maximum number of elements that will be indexed in a single bulk request. Optional.

    | `Default: 5000`

  elasticsearch.limit.commit
    The maximum number of elements to limit commit objects. Set this to a reasonable size in order to avoid object size limitations in Elasticsearch.

    | `Default: 10000`

LDAP Configuration
------------------

  ldap.enabled
    Enable the LDAP plugin. Required.

  ldap.provider.base
    The base string to use. Required.

  ldap.provider.url
    The provider url, including the base. Required.

  ldap.provider.userdn
    The userdn to use to authenticate to the provider. Optional.

    | `Default: null`

  ldap.provider.password
    The password to use to authenticate to the provider. Optional.

    | `Default: null`

  ldap.user.dn.pattern
    The dn pattern for the user. Can accept multiple patterns for separate branches, delimited by ';'. Required.

    | `Default: uid={0}`

  ldap.user.attributes.username
    The attribute to use for the username. Optional.

    | `Default: uid`

  ldap.user.attributes.email
    The attribute to use for the email address. Optional.

    | `Default: mail`

  ldap.group.role.attribute
    The attribute to use for the group role. Optional.

    | `Default: cn`

  ldap.group.search.base
    The base for group search. Optional.

    | `Default: ''`

  ldap.group.search.filter
    The search filter for group search. Optional.

    | `Default: (uniqueMember={0})`


Storage Configuration
---------------------

If aws s3 environment is used and s3.access_key and s3.secret_key are not defined, credentials will be taken according to the `aws default credential provider chain <https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html>`_.

  s3.endpoint
    Endpoint of S3 compliant storage service

  s3.access_key
    This is the access key for the S3 bucket. Required (Optional if using AWS).

  s3.secret_key
    This is the secret key for the S3 bucket. Required (Optional if using AWS).

  s3.region
    This is the region that the S3 bucket is located in. Required.

  s3.bucket
    This is the name of the S3 bucket. Optional.

    | `Default: mms`

SSL & HSTS
----------

  server.ssl.enabled
    To enable SSL, place a valid keystore on the classpath then set this option to true.

  server.ssl.key-alias
    SSL name of the key alias to use.

  server.ssl.key-store
    SSL Key store filename / location

  server.ssl.key-store-password
    The password for the key store.

  mms.hsts.enabled
    Enable HSTS. Must have a valid SSL.
