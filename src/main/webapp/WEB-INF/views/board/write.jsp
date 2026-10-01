<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>게시글 작성</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f6f7fb;
            margin: 0;
            padding: 40px;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            background: #fff;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.08);
        }
        h2 {
            margin-top: 0;
            margin-bottom: 20px;
        }
        .form-group {
            margin-bottom: 16px;
        }
        label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
        }
        input[type="text"],
        input[type="password"],
        textarea {
            width: 100%;
            box-sizing: border-box;
            padding: 10px 12px;
            border: 1px solid #d5d9e2;
            border-radius: 6px;
            font-size: 14px;
        }
        textarea {
            min-height: 220px;
            resize: vertical;
        }
        .btn-wrap {
            margin-top: 20px;
            text-align: right;
        }
        button {
            padding: 10px 18px;
            border: none;
            border-radius: 6px;
            background-color: #2f6fed;
            color: #fff;
            cursor: pointer;
            font-size: 14px;
        }
        button.cancel {
            background-color: #6c757d;
            margin-right: 8px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>게시글 작성</h2>

        <form action="${pageContext.request.contextPath}/board/write" method="post">
            <div class="form-group">
                <label for="title">제목</label>
                <input type="text" id="title" name="title" placeholder="제목을 입력하세요" required>
            </div>

            <div class="form-group">
                <label for="writer">작성자</label>
                <input type="text" id="writer" name="writer" placeholder="작성자명을 입력하세요" required>
            </div>

            <div class="form-group">
                <label for="password">비밀번호</label>
                <input type="password" id="password" name="password" placeholder="비밀번호를 입력하세요" required>
            </div>

            <div class="form-group">
                <label for="content">내용</label>
                <textarea id="content" name="content" placeholder="내용을 입력하세요" required></textarea>
            </div>

            <div class="btn-wrap">
                <button type="button" class="cancel" onclick="history.back();">취소</button>
                <button type="submit">등록</button>
            </div>
        </form>
    </div>
</body>
</html>
