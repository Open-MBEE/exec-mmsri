.. _users:

==========
User Management
==========

Creating Local Users
--------------------
  Post request to /user
  ::
    {
      "username": "testuser",
      "password": "testpass",
      "email": "test@test.com",
      "firstname": "Test",
      "lastname": "User",
      "admin": false
    }

  ::

    curl -X POST "https://cae-mms-test.jpl.nasa.gov/user" -H  "accept: application/json" -H  "Authorization: Basic YWRtaW46bjlXVzJwNlg=" -H  "Content-Type: application/json" -d "{\"username\":\"testuser\",\"password\":\"testpass\",\"email\":\"test@test.com\",\"firstname\":\"Test\",\"lastname\":\"User\",\"admin\":false}"

Granting user permissions to an existing organization
-----------------------------------------------------
  Post request to /orgs/{orgId}/permissions
  ::
    {
      "users": {
        "action": "MODIFY",
        "permissions": [
          {
            "name": "testuser",
            "role": "READER | WRITER | ADMIN"
          }
        ]
      },
      "inherit": true
    }

  ::

    curl -X POST "https://cae-mms-test.jpl.nasa.gov/orgs/testorg/permissions" -H  "accept: application/json" -H  "Authorization: Basic YWRtaW46bjlXVzJwNlg=" -H  "Content-Type: application/json" -d "{\"users\":{\"action\":\"MODIFY\",\"permissions\":[{\"name\":\"testuser\",\"role\":\"WRITER\"}]},\"inherit\":true}"

Permissions
===========
Out of the box, MMS offers 3 roles that can be used to grant access to models.

Admin - Can read, write and administer permissions

Writer - Can read and write to selected organizations and projects

Reader - Can read from selected organizations and projects

Apart from the roles, permissions are enforced at 2 hierarchical levels, organization and project. Permissions are inherited from an organization, and will override project permissions. For instance, if a user has the Writer role on an organization, the user should not be given the Reader role on a project that is contained in the organization. Further documentation on permissions can be found here.