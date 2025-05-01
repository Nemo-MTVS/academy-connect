// README 파일 서버로 업로드
function uploadReadme() {
    const fileInput = document.getElementById('readmeFile');
    const file = fileInput.files[0];

    if (!file) {
        alert("파일을 선택해주세요!");
        return;
    }

    const formData = new FormData();
    formData.append("file", file);

    fetch("/upload-readme", {
        method: "POST",
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("업로드 실패");
            }
            return response.text();
        })
        .then(text => {
            const previewBox = document.getElementById('markdownPreview');
            previewBox.innerHTML = marked.parse(text); // 마크다운 렌더링
        })
        .catch(error => {
            console.error("에러 발생:", error);
            alert("파일 업로드 중 오류가 발생했습니다.");
        });
}