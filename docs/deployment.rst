.. _deployment:

==========
Deployment
==========

Officially Supported Build
-----------------------------------
There are two officially supported builds.

Building MMSRI
--------------
There are a number of ways to use MMSRI

Deployment Considerations
-------------------------

Minimum Specification
=====================
In terms of running MMS, the most important component is RAM, as the application and it’s dependencies are not very CPU intensive, aside from a very few functions. It is recommended to run the MMS4 application itself on an instance that has at least 4GB of RAM available for use, regardless of running either the Docker image or the executable JAR. The reason for this is to mitigate any large model posts (up to 4GB in size) which are sent from any of the clients. Although MMS4 is capable of ingesting chunked post requests, clients may not have that feature implemented (i.e. Cameo MDK).

The minimum recommended RAM for the Postgresql database as well as Elasticsearch is at least 2GB, which may need to increase as models tend to inflate over time. If running MinIO in a production environment, that application should also be allocated 2 GB RAM.

Scalability
===========
The use of the MMS docker container, and cloud services is highly recommended, as they allow for maximum scalability. Below you will find a brief overview of the deployment of MMS4 at JPL.

Authentication
==============
One area of concern should be the authentication mechanism that is chosen. Out of the box, MMS offers LDAP integration, which requires configuration of the LDAP servers and bind information, as well as TeamWorkCloud integration, which requires configuration of a TeamWorkCloud server. Further documentation is located at LDAP and TWC module ReadTheDocs pages.

Permissions
===========
Out of the box, MMS offers 3 roles that can be used to grant access to models.

Admin - Can read, write and administer permissions

Writer - Can read and write to selected organizations and projects

Reader - Can read from selected organizations and projects

Apart from the roles, permissions are enforced at 2 hierarchical levels, organization and project. Permissions are inherited from an organization, and will override project permissions. For instance, if a user has the Writer role on an organization, the user should not be given the Reader role on a project that is contained in the organization. Further documentation on permissions can be found here.