========================================
MySQL 数据库初始化快速指南
========================================

【适用版本】MySQL 5.7 / 8.0

【步骤1】配置MySQL环境变量
-----------------------------------------
1. 找到MySQL安装目录的bin文件夹：
   MySQL 5.7: C:\Program Files\MySQL\MySQL Server 5.7\bin
   MySQL 8.0: C:\Program Files\MySQL\MySQL Server 8.0\bin

2. 添加到系统PATH环境变量：
   - 右键"此电脑" → "属性" → "高级系统设置"
   - 点击"环境变量"
   - 在"系统变量"中找到"Path"，点击"编辑"
   - 点击"新建"，粘贴MySQL的bin目录路径
   - 点击"确定"保存

3. 重新打开cmd验证：
   mysql --version

【步骤2】启动MySQL服务
-----------------------------------------
打开cmd（管理员权限），执行：

MySQL 5.7:
net start MySQL57

MySQL 8.0:
net start MySQL80

【步骤3】初始化数据库
-----------------------------------------
方法一：使用批处理脚本（推荐）
1. 打开cmd（不是PowerShell！）
2. cd d:\Gradetwo\andriod\demo2\newsApp
3. init_database.bat
4. 输入MySQL密码

方法二：手动执行SQL
1. mysql -u root -p
2. source d:/Gradetwo/andriod/demo2/newsApp/src/main/resources/schema.sql
3. source d:/Gradetwo/andriod/demo2/newsApp/src/main/resources/data.sql
4. exit

【步骤4】修改配置文件
-----------------------------------------
编辑：newsApp/src/main/resources/application.yml

修改数据库密码：
spring:
  datasource:
    password: 你的MySQL密码

【步骤5】启动应用
-----------------------------------------
cd d:\Gradetwo\andriod\demo2\newsApp
mvn clean install
mvn spring-boot:run

【步骤6】测试
-----------------------------------------
浏览器访问：http://localhost:8080/api/news

========================================
常见错误解决
========================================

错误1：'mysql' 不是内部或外部命令
解决：配置MySQL环境变量（见步骤1）

错误2：Access denied for user 'root'
解决：检查application.yml中的密码是否正确

错误3：Table 'news_db.news' doesn't exist
解决：重新执行init_database.bat

错误4：Communications link failure
解决：检查MySQL服务是否启动（见步骤2）

========================================
详细文档
========================================
请查看：5.数据库集成说明.md
