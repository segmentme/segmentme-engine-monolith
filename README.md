# segmentme
 ### Glosarry
 
 1. **User**:
 Person registered in System (plain registration or via Auth provider like Auth0,Google)
 1. **Workpsace**:
 Working area which  unite different users to work on set of conditions, working area define unique configuration.
 1. **User profile**
 Workspace user with assigned Role
 1. **Integration point**: 
 unique key for user integration through API
 1. **Analysis context**:
 User specific request to the system to be analyzed. 
 1. **Context Schema**:
 defined types of analysis context fields 
 1. **Condition**:
 Logical action which applies to one criteria from Analysis context.
 1. **Analysis Rule**:
 Set of conditions which need to be calculated to determine result for the specific key.
 
 ##Workflow
 ### Begin
 Register new user. 
 When registration complete you will get an access to your personal default workspace.
 *Personal workspace could have only 1 integration point.
 Integration point is a token which is using to analyze only 1 type of schema from 1 consumer .
 
 For example you have WEB and MOBILE applications, and you want to split analysis per platform, 
 due to each  platform can provide own set of data for analysis to identify features available for particular user.
 
 In this case you will need to create 2 integration points due to only 1  [Context Schema](#context-schema-creation) can be created within 1 integration point.
 
 Each workspace created with default configuration, which included known date formats,
 you can extend supported datetime formats as you needed.
 
 ### Context schema creation 
 Context schema is a king of your payload description from field-type perspective,
 Schema will allow you to validate conditions criteria when you setting-up your rules/conditions.
 In some cases schema will help engine to resolve conflicts between types during analysis,
 for example differences between DATE and STRING fields.  
 
 To resolve your schema really quick you may feed example of your JSON payload which you want to analyze.
 i.e:
 ```json
 {
   "user": {
     "numbersArray": [
       12,
       23,
       22.4
     ],
     "email": "vladislavkondratenko@coherentsolutions.com",
     "name": "Vladislav",
     "status": "ACTIVE",
     "fullAge": 12,
     "weight": 199999999.123232,
     "details": {
       "gender": "",
       "address": {
         "addressLine1": "Dasdsadas",
         "state": "NU"
       },
       "birthDate": "2006-10-22",
       "phone": "213123"
     }
   },
   "stringArray": [
     "11",
     "44"
   ],
   "objectArrays": [
     {
       "id": "123",
       "agreementNumber": 123,
       "isActive": true,
       "numbersArray": [
         1,
         2,
         3
       ],
       "subObjects": [
         {
           "subObjectId": "id1",
           "array": ["a1","a2","a3"]
         },
         {
           "subObjectId": "id2",
           "array": ["a4","a5"]
         }
       ]
     },
     {
       "id": "431",
       "isActive": false,
       "dateTime": "2010-01-01T12:00:13Z",
       "numbersArray": [
         12,
         23,
         22.4
       ],
       "subObjects": [
         {
           "subObjectId": "id3",
           "array": ["a6"]
         }
       ]
     }
   ]
 }
 
 ``` 
 
 As you see there is a lot of objects/subobjects field types etc. 
 but final schema will looks like:
 
 | nodeName | type | 
 |------------|------------|
 |user                                | OBJECT                          |
 |user.numbersArray                   | ARRAY of NUMBERS                |
 |user.email                          | STRING                          |
 |user.name                           | STRING                          |
 |user.details                        | OBJECT                          |
 |user.details.gender                 | STRING                          |
 |user.details.address                | OBJECT                          |
 |user.details.address.addressLine1   | STRING                          |
 |user.details.address.state          | STRING                          |
 |user.details.birthDate              | DATE                            |
 |user.details.phone                  | STRING                          |
 |stringArray                         | ARRAY of STRING                 |
 |objectArrays                        | ARRAY of OBJECT                 |
 |objectArrays.id                     | STRING                          |
 |objectArrays.agreementNumber        | NUMBER                          |
 |objectArrays.subObjects             | ARRAY, of OBJECT                |
 |objectArrays.subObjects.array       | ARRAY of STRING                 |
 |objectArrays.subObjects.subObjectId | STRING                          |
 |objectArrays.numbersArray           | ARRAY of NUMBER                 |
 |objectArrays.isActive               | BOOLEAN                         |
 |objectArrays.dateTime               | DATE                            |
 |user.status                         | STRING                          |
 |user.fullAge                        | NUMBER                          |
 |user.weight                         | NUMBER                          |
 
 
 As you see node with name `user.details.birthDate` and `objectArrays.dateTime` defined as DATE,
 that's because such date formats known by Workspace configuration , you can extend dates formats whenever you want.
 
 If this schema looks good, you can save it and link to the integration-point.
 Only one schema can be linked to one integration point. 
 
