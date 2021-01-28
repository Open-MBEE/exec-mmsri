# API changes from MMS 3.x

take out any `/alfresco/service` prefix from apis

## Login and auth

WAS: 

    POST {"username": "user", "password": "pass"} to /api/login
 
    returns {"data": {"ticket": "ticket"}
 
    add ?alf_ticket=ticket to any subsequent request urls
 
IS:

    POST {"username": "user", "password": "pass"} to /authentication

    returns {"token": "token"}

    add Bearer token to Authorization header in subsequent requests

If authenticating some other way, can get token by using

    GET /authentication
    
## Project Creation

WAS:

    POST {"projects": [{"id": "projectId", "name": "project name", "orgId": "orgId"}]} to /orgs/orgid/projects
    
IS:

    POST {"projects": [{"id": "projectId", "name": "project name", "orgId": "orgId", "schema": "cameo"}]} to /projects

## Get Projects by Org

WAS:

    GET /orgs/{orgId}/projects
    
IS:

    GET /projects?orgId={orgId} 

## Artifacts

WAS:

Used to have its own set of `/projects/{projectId}/refs/{refId}/artifacts` urls

IS:

Artifacts are treated as "attachments" to an element

    FORM POST to /projects/{projectId}/refs/{refId}/elements/{elementId} with file
    
see [Artifacts](https://github.com/Open-MBEE/mms/tree/develop/artifacts)

To get the file back

    GET /projects/{projectId}/refs/{refId}/elements/{elementId}/{extension} or
    GET /projects/{projectId}/refs/{refId}/elements/{elementId} with mimetype in Accept header

## Search

WAS: 

Search accepts elasticsearch query

IS:

See OpenAPI (subject to change)

Search will search only elements in specified project and ref

## Misc

### Depth/recurse param in element get

WAS:

add `?depth=#` or `recurse=true` to `GET` `/projects/{projectId}/refs/{refId}/elements/{elementId}`

IS:

param removed, use search api's recurse function
    
    POST /projects/{projectId}/refs/{refId}/search
    {
    	"params" : {
    		"ownerId" : "x"
    	},
    	"recurse" : {
    		"id" : "ownerId"
    	}
    }
    
### New Apis (see OpenAPI)

Permissions

Auth (create local user and change password)

Webhooks (also see [Webhooks](https://github.com/Open-MBEE/mms/tree/develop/webhooks))
