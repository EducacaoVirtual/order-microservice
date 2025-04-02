#### Educação Virtual
## Order API
[![Language](https://img.shields.io/badge/linguagem-Java-red)](https://www.python.org/)
[![Language](https://img.shields.io/badge/Spring-SpringBoot3-blue)](https://www.python.org/)
[![Language](https://img.shields.io/badge/Spring-SpringDataJPA-orange)](https://www.python.org/)
[![Language](https://img.shields.io/badge/database-PostgreSQL-green)](https://www.python.org/)
[![License](https://img.shields.io/badge/license-MIT-yellow)](https://opensource.org/licenses/MIT)
[![License](https://img.shields.io/badge/repository-git-white)](https://github.com/alexncosta/addressbookapp.git)

### Prerequisites:
* Java 21
* Maven 3.6.3+
* OpenAPI Generator 7.9.0
* PostgreSQL
* OpenaAPI 3.0.3

### ✨ Features:
- [x] Endpoint to create new orders
- [x] Endpoint to update orders
- [x] Endpoint to get all orders
- [x] Endpoint to get order by id

### 🔨 How can you execute:
**Clone repository**<br />
```
$ git clone https://github.com/alexncosta/addressbookapp.git
```
<br />
In the root directory of the project, execute the following command:<br />
<b>mvn clean install</b>

After the execution of the command, the project will be compiled and the tests will be executed.
If everything is correct, a message similar to the one shown below will be displayed:
```
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  6.210 s
[INFO] Finished at: 2025-01-28T16:43:08-03:00
[INFO] ------------------------------------------------------------------------
```
<br />
<b>Go to the target directory and execute the following commands:</b><br />

```
cd target/
java -jar order-api-1.0-SNAPSHOT.jar
```
