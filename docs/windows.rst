.. _windows:

====================
Windows Installation
====================

Setup
=====

This document describes how to configure and start up a running version of MMS 4 using the MMS Reference Implementation (MMSRI).

Versions Used to Test these instructions


* `MMSRI v4.0.0-b2 <https://github.com/Open-MBEE/mmsri/tree/4.0.0-b2>`_
* Windows 10 (64-bit) 10 Enterprise (v 10.0.19041 Build 19041)
* Docker Desktop (Community Edition) v2.5.0.0

  * Docker Engine v19.03.13
  * Docker-Compose v1.27.4

1. Download and install Docker Desktop
--------------------------------------

..

   If you already have a fairly modern version of Docker Desktop installed you can skip this step.


Download the `Docker Desktop Community 2.5.0.0 <https://docs.docker.com/docker-for-windows/release-notes/#docker-desktop-community-2500>`_ installer and follow the installation instructions.

2. Download and install Git for Windows
---------------------------------------

..

   If you already have Git you can skip this step.


Go to the `Git for Windows <https://gitforwindows.org/>`_ site and download the latest version.

This will not only install Git, but will also provide Git BASH, a shell command for working with Git in Windows.

3. Clone the MMS 4 Repository
-----------------------------

..

   If you already cloned the repository, make sure you are on the correct branch/tag by following the checkout step (4).


Clone the repository and checkout the latest tagged version, as of the writing of these instructions.


#. 
   Open GitBash

#. 
   Navigate to the folder you want to clone the repo on, e.g., ``C:\repos\mmsri``\ , but you may need to clone in your user folder if you do not have permission to create a new folder at the root drive level, for example :

   .. code-block:: bash

       cd /c/repos/mmsri

   ..

      If you have to use a deeply nested folder, make sure you check out the "Windows Maximum Path Length Issue" entry in the Troubleshooting section below, this will explain how to extend Windows' maximum path length.


#. 
   Clone the ``MMS Reference Implementation`` repository

   .. code-block:: bash

       git clone git@github.com:Open-MBEE/mmsri.git

#. 
   Checkout the right branch/tag:

   .. code-block:: bash

       git checkout 4.0.0-b2

4. Get setup
------------

..

   IMPORTANT: Windows PowerShell is the recommended shell for the remaining steps.



#. Open "Windows PowerShell".

#. Navigate to the folder where you cloned the ``mmsri`` repository (e.g., ``C:\repos\mmsri``\ )

#. Make sure that ``docker-compose`` is available by running:

   .. code-block:: bash

       docker-compose --version

    You should see something like this:

   .. code-block:: bash

       docker-compose version 1.27.4, build 40524192

5. Start and Monitor MMS
------------------------


#. 
   Start up the containers

   .. code-block:: bash

       docker-compose up -d

    You should see something like this:

   .. code-block:: bash

       Creating network "mmsri_default" with the default driver
       Creating mmsri_minio_1         ... done
       Creating mmsri_postgres_1      ... done
       Creating mmsri_elasticsearch_1 ... done
       Creating mms                   ... done

   ..

      If you are running this for the first time, you will see a much longer output.

      If you modify the ``application.properties`` configuration file you will have to rebuild the ``MMS`` image by running: ``docker-compose up build``


#. 
   Make sure the containers are running:

   .. code-block:: bash

       docker-compose ps

    You should see something like this:

   .. code-block:: bash

               Name                       Command               State                       Ports
       ---------------------------------------------------------------------------------------------------------------
       mms                     java -Djdk.tls.client.prot ...   Up      0.0.0.0:8080->8080/tcp
       mmsri_elasticsearch_1   /tini -- /usr/local/bin/do ...   Up      0.0.0.0:9200->9200/tcp, 0.0.0.0:9300->9300/tcp
       mmsri_minio_1           /usr/bin/docker-entrypoint ...   Up      0.0.0.0:9000->9000/tcp
       mmsri_postgres_1        docker-entrypoint.sh postgres    Up      0.0.0.0:5432->5432/tcp

#. 
   Or you can monitor what the server is doing by tailion and following the log for the ``mms`` container by:

   .. code-block:: bash

       docker-compose logs -t -f mms

    which should show something like this:

   .. code-block:: bash

       Attaching to mms
       mms              | 2020-11-19T03:08:38.615040700Z
       mms              | 2020-11-19T03:08:38.628149200Z   .   ____          _            __ _ _
       mms              | 2020-11-19T03:08:38.628227300Z  /\\ / ___'_ __ _ _(_)_ __  __ _ \ \
       mms              | 2020-11-19T03:08:38.628232500Z ( ( )\___ | '_ | '_| | '_ \/ _` | \ \
       mms              | 2020-11-19T03:08:38.628239900Z  \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
       mms              | 2020-11-19T03:08:38.628243000Z   '  |____| .__|_| |_|_| |_\__, | / / / /
       mms              | 2020-11-19T03:08:38.628245800Z  =========|_|==============|___/=/_/_/_/
       mms              | 2020-11-19T03:08:38.649950000Z  :: Spring Boot ::        (v2.3.2.RELEASE)
       mms              | 2020-11-19T03:08:38.650075200Z
       mms              | 2020-11-19T03:08:39.160885900Z 2020-11-19 03:08:39.151  INFO 1 --- [           main] org.openmbee.mms.mmsri.MMSRIApplication  : Starting MMSRIApplication on mms with PID 1 (/app.jar started by root in /mms)
       mms              | 2020-11-19T03:08:39.161316700Z 2020-11-19 03:08:39.161  INFO 1 --- [           main] org.openmbee.mms.mmsri.MMSRIApplication  : The following profiles are active: test
       mms              | 2020-11-19T03:08:42.769460100Z 2020-11-19 03:08:42.767  INFO 1 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
       mms              | 2020-11-19T03:08:43.167079600Z 2020-11-19 03:08:43.166  INFO 1 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 374ms. Found 15 JPA repository interfaces.
       mms              | 2020-11-19T03:08:45.885452700Z 2020-11-19 03:08:45.881  INFO 1 --- [           main] trationDelegate$BeanPostProcessorChecker : Bean 'persistenceJPAConfig' of type [org.openmbee.mms.rdb.config.PersistenceJPAConfig$$EnhancerBySpringCGLIB$$ca626494] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
       mms              | 2020-11-19T03:08:46.187477700Z 2020-11-19 03:08:46.187  INFO 1 --- [           main] trationDelegate$BeanPostProcessorChecker : Bean 'org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler@76b74e9c' of type [org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
       mms              | 2020-11-19T03:08:46.216919800Z 2020-11-19 03:08:46.216  INFO 1 --- [           main] trationDelegate$BeanPostProcessorChecker : Bean 'methodSecurityMetadataSource' of type [org.springframework.security.access.method.DelegatingMethodSecurityMetadataSource] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)

6. Use MMS
----------

Refer to the MMS documentation, but to test if the service is running, you can go to: `\ ``http://localhost:8080/actuator/health`` <http://localhost:8080/actuator/health>`_\ , which should show ``healthy``.

Check out the REST API
^^^^^^^^^^^^^^^^^^^^^^


#. 
   Go to `\ ``http://localhost:8080/v3/swagger-ui.html`` <http://localhost:8080/v3/swagger-ui.html>`_ to see more info on the API.

#. 
   At the top of the page click on the green outline ``Authorize`` button to see the "Available Authorizations" modal window.

#. 
   Use the ``basicAuth (http, Basic)`` option to authenticate the session by using ``test`` for both the Username and Password.

   ..

      After you click the "Authorize" button, you should see its label change to ``Logout``.


#. 
   Click the ``Close`` button, and you are now logged in the API.

   ..

      All the endpoints should now show a closed black lock.


Configuring MMS
===============

..

   IMPORTANT: This reference configuration is **not intended to be used for production environments**.


With that said, it is possible to configure MMS with this pre-built docker image by defining appropriately named environment variables in the docker compose file.

To do so, study the `\ ``application-test.properties`` <https://github.com/Open-MBEE/mmsri/blob/develop/src/main/resources/application-test.properties>`_ configuration file, and determine `how to define associated ENVIRONMENT VARIABLES <https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config-relaxed-binding-from-environment-variables>`_ in the ``mms`` service in the ``docker-compose.yml`` file.

For example, you could specify a different username/password for MMS by modifying the `\ ``docker-compose.yml`` <https://github.com/Open-MBEE/mmsri/blob/develop/docker-compose.yml>`_ file as follows:

.. code-block:: yaml

     ...
     mms:
       build: .
       container_name: mms
       hostname: mms
       environment:
         - "SPRING_PROFILES_ACTIVE=test"
         - "SDVC_ADMIN_USERNAME=type_your_username_here"
         - "SDVC_ADMIN_PASSWORD=type_your_password_here"
     ...

Troubleshooting
===============

This section covers some of the most critical issues you may face when setting up MMS in a Windows Environment.

Windows Maximum Path Length Issue
---------------------------------

By default Windows, does not allow paths to be longer than 260 characters.

If you see a copy error when building the custom CET JupyterLab or the installers in Windows, it may be because one of the paths is over 260 characters long.

How to Fix This
^^^^^^^^^^^^^^^


#. Open the Registry Editor (\ ``regedit.exe``\ ) in **Admin mode**
   ..

      If you do not have admin mode, you may be able to ask your support IT department to provide you with a local admin, or do this fix for you.


#. Navigate to: ``HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\FileSystem``
#. Change the value of the ``LongPathsEnabled`` variable from ``0`` to ``1``
#. Restart your computer and try again...
