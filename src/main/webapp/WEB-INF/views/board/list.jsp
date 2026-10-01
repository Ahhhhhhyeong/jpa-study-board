<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>Board List</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 40px;
            background-color: #f7f7f7;
        }
        h1 {
            color: #333;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            background-color: #fff;
            box-shadow: 0 0 5px rgba(0,0,0,0.1);
        }
        th, td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: center;
        }
        th {
            background-color: #f2f2f2;
        }
        a {
            text-decoration: none;
            color: #007bff;
        }
        .top-bar {
            margin-bottom: 20px;
        }
        .write-btn {
            display: inline-block;
            padding: 8px 14px;
            background-color: #007bff;
            color: #fff;
            border-radius: 4px;
        }
    </style>
</head>
<body>
    <h1>게시판 목록</h1>

    <div class="top-bar">
        <a href="${pageContext.request.contextPath}/board/write" class="write-btn">글 작성</a>
    </div>

    <table>
        <thead>
            <tr>
                <th>번호</th>
                <th>제목</th>
                <th>작성자</th>
                <th>작성일</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="board" items="${boardList}">
                <tr>
                    <td>${board.id}</td>
                    <td><a href="${pageContext.request.contextPath}/board/view/${board.id}"><c:out value="${board.title}" /></a></td>
                    <td><c:out value="${board.writer}" /></td>
                    <td class="created-at" data-created-at="${board.createdAt}"></td>
                </tr>
            </c:forEach>
            <c:if test="${empty boardList}">
                <tr><td colspan="4">게시글이 없습니다.</td></tr>
            </c:if>
        </tbody>
    </table>
    <nav aria-label="페이지 이동">
        <c:if test="${pageNumber > 1}">
            <a href="${pageContext.request.contextPath}/board/list?pageNumber=${pageNumber - 1}">이전</a>
        </c:if>
        <span>현재 ${pageNumber}페이지 / 전체 ${totalPages}페이지</span>
        <c:if test="${pageNumber < totalPages}">
            <a href="${pageContext.request.contextPath}/board/list?pageNumber=${pageNumber + 1}">다음</a>
        </c:if>
    </nav>

    <script>
        document.querySelectorAll('.created-at').forEach(ele => {
            const value = ele.dataset.createdAt;

            if(!value) {
                ele.textContent = '-';
                return;
            }

            ele.textContent = value.replace("T", ' ').slice(0, 16);
        });
    </script>
</body>
</html>
