.. _installation:

============
Installation
============

The MMS, in it's reference implementation configuration currently has two main dependencies\:

  - Relational Database (e.g. Postgresql, MySQL, etc.)
  - Document Store (e.g. Elasticsearch)
  - Optionally, for artifact storage, an S3 compliant object store is necessary. We develop against MinIO and deploy against AWS S3, however, any S3 compliant object store will suffice. (e.g. MinIO)


Dependencies
------------

This guide assumes that you have setup and configured the following dependencies and that the server or instance the MMS RI application will have access to the services.

  - Java 11+
  - Postgresql 11 or MySQL 5.7+
  - Elasticsearch 7.8+
  - S3 Bucket (either provided by AWS or by MinIO in case of local storage)

.. _linux:

Bare Metal Installation
-----------------------

In the following steps, replace $VERSION with the latest semantic version released.

  1. Download the prebuilt jar from the `releases page <https://github.com/Open-MBEE/mmsri/releases>`_.
      ::

        wget https://github.com/Open-MBEE/mms/archive/$VERSION.jar -O $VERSION.jar

  2. Download the example `properties file <https://github.com/Open-MBEE/mmsri/blob/develop/src/main/resources/application.properties.example>`_.
      ::

        wget https://github.com/Open-MBEE/mms/archive/application.properties -O application.properties

  3. Edit the properties file according to :ref:`configuration`
       * Note you may rename the properties file to any filename

  4. Create a subdirectory called `config` in the same directory as the prebuilt jar file and place the edited properties file within it.
      ::

        mkdir config;
        mv application.properties ./config/

      * Alternatively, you can define the location that MMS will look for the properties file by defining the environment variable SPRING_CONFIG_LOCATION.

  5. Run the application.
      ::

        java -jar $VERSION-.jar


Docker based Installation
-------------------------

In the following steps, replace $VERSION with the latest semantic version released.

  1. Download the example `properties file <https://github.com/Open-MBEE/mmsri/blob/develop/src/main/resources/application.properties.example>`_.

  2. Edit the properties file according to :ref:`configuration`
       * Note you may rename the properties file to any filename

  3. Create a container with the latest docker image. The configuration below will use the host network and will be named `mms`.
      ::

        docker create --name=mms --network="host" -e "SPRING_CONFIG_LOCATION=/application.properties" openmbee/mms:$VERSION

  4. Copy your properties file into the container.
      ::

        docker cp application.properties mms:/application.properties

  5. Start your container.
      ::

        docker start mms


Windows Installation
--------------------

For Windows docker installation, see :ref:`windows`
