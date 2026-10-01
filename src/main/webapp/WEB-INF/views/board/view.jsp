<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>게시글 보기</title>
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
        h2 { margin-top: 0; margin-bottom: 20px; }
        .field { margin-bottom: 16px; }
        .field-label { display: block; margin-bottom: 8px; font-weight: bold; }
        .field-value {
            padding: 10px 12px;
            border: 1px solid #d5d9e2;
            border-radius: 6px;
            font-size: 14px;
            overflow-wrap: anywhere;
        }
        .content { min-height: 220px; white-space: pre-wrap; line-height: 1.6; }
        .btn-wrap { margin-top: 20px; text-align: right; }
        .button {
            display: inline-block;
            padding: 10px 18px;
            border-radius: 6px;
            background-color: #2f6fed;
            color: #fff;
            text-decoration: none;
            font-size: 14px;
        }
        .button.list { background-color: #6c757d; margin-right: 8px; }
    </style>
</head>
<body>
    <main class="container">
        <h2>게시글 보기</h2>
        <div class="field">
            <span class="field-label">제목</span>
            <div class="field-value"><c:out value="${board.title}" /></div>
        </div>
        <div class="field">
            <span class="field-label">작성자</span>
            <div class="field-value"><c:out value="${board.writer}" default="-" /></div>
        </div>
        <div class="field">
            <span class="field-label">작성일</span>
            <div class="field-value date" data-date="${board.createdAt}"><c:out value="${board.createdAt}" /></div>
        </div>
        <div class="field">
            <span class="field-label">수정일</span>
            <div class="field-value date" data-date="${board.updatedAt}"><c:out value="${board.updatedAt}" /></div>
        </div>
        <div class="field">
            <span class="field-label">내용</span>
            <div class="field-value content"><c:out value="${board.content}" /></div>
        </div>
        <div class="btn-wrap">
            <a class="button list" href="${pageContext.request.contextPath}/board/list">목록</a>
            <a class="button" href="${pageContext.request.contextPath}/board/write">글 작성</a>
        </div>
	</main>
    <script>
        document.querySelectorAll('.date').forEach(element => {
            const value = element.dataset.date;
            element.textContent = value ? value.replace('T', ' ').slice(0, 16) : '-';
        });
    </script>
</body>
</html>
