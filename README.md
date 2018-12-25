# 说明
## EditTextView 3种输入框 银行卡号每四位自动加空格，过滤表情，附带清空删除
### ContainsEmojiEditText  过滤大部分表情
### EditTextWithDelete  过滤大部分表情，并输入内容显示一键清空ICON
### SpaceEditText  每X位自动添加在后面添加空格的EditText 并带有一键清空icon,同时过滤表情
implementation 'com.github.missqiyue:EditTextView:v1.0'
**1. 是否显示一键清空icon** <br>
       setDeleteIconShow(false)<br>
**2. 输入监听** <br>
        setTextChangeListener()<br>
**3. 属性设置** <br>
        是否显示一键清空icon<br>
        app:isShowDelete="true"<br>
        每隔多少个字节添加空格<br>
        app:spaceSplitCount="3"<br>
