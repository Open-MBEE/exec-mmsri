
API changes from MMS 3.x
========================

take out any ``/alfresco/service`` prefix from apis

Login and auth
--------------

WAS: 

.. code-block::

   POST {"username": "user", "password": "pass"} to /api/login

   returns {"data": {"ticket": "ticket"}

   add ?alf_ticket=ticket to any subsequent request urls


IS:

.. code-block::

   POST {"username": "user", "password": "pass"} to /authentication

   returns {"token": "token"}

   add Bearer token to Authorization header in subsequent requests


If authenticating some other way, can get token by using

.. code-block::

   GET /authentication


Project Creation
----------------

WAS:

.. code-block::

   POST {"projects": [{"id": "projectId", "name": "project name", "orgId": "orgId"}]} to /orgs/orgid/projects


IS:

.. code-block::

   POST {"projects": [{"id": "projectId", "name": "project name", "orgId": "orgId", "schema": "cameo"}]} to /projects


Get Projects by Org
-------------------

WAS:

.. code-block::

   GET /orgs/{orgId}/projects


IS:

.. code-block::

   GET /projects?orgId={orgId} 


Artifacts
---------

WAS:

Used to have its own set of ``/projects/{projectId}/refs/{refId}/artifacts`` urls

IS:

Artifacts are treated as "attachments" to an element

.. code-block::

   FORM POST to /projects/{projectId}/refs/{refId}/elements/{elementId} with file


see `Artifacts <https://github.com/Open-MBEE/mms/tree/develop/artifacts>`_

To get the file back

.. code-block::

   GET /projects/{projectId}/refs/{refId}/elements/{elementId}/{extension} or
   GET /projects/{projectId}/refs/{refId}/elements/{elementId} with mimetype in Accept header


Search
------

WAS: 

Search accepts elasticsearch query

IS:

See OpenAPI (subject to change)

Search will search only elements in specified project and ref

Misc
----

Depth/recurse param in element get
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

WAS:

add ``?depth=#`` or ``recurse=true`` to ``GET`` ``/projects/{projectId}/refs/{refId}/elements/{elementId}``

IS:

param removed, use search api's recurse function

.. code-block::

   POST /projects/{projectId}/refs/{refId}/search
   {
       "params" : {
           "ownerId" : "x"
       },
       "recurse" : {
           "id" : "ownerId"
       }
   }


New Apis (see OpenAPI)
^^^^^^^^^^^^^^^^^^^^^^

Permissions

Auth (create local user and change password)

Webhooks (also see `Webhooks <https://github.com/Open-MBEE/mms/tree/develop/webhooks>`_\ )
