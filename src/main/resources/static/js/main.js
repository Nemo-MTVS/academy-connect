// 문서가 로드된 후 실행
document.addEventListener('DOMContentLoaded', function() {
    console.log('Academy Connect 웹사이트가 로드되었습니다.');
    
    // 드롭다운 초기화 (이미 Bootstrap JS가 처리하지만, 커스텀 기능 추가 가능)
    const dropdowns = document.querySelectorAll('.dropdown-toggle');
    if (dropdowns) {
        console.log('드롭다운 메뉴가 초기화되었습니다.');
    }
    
    // 현재 메뉴 활성화 표시
    highlightCurrentMenu();
});

// 현재 URL 기반으로 메뉴 하이라이트
function highlightCurrentMenu() {
    const currentPath = window.location.pathname;
    
    // 메뉴 링크 요소들
    const menuLinks = document.querySelectorAll('.nav-link');
    
    menuLinks.forEach(link => {
        const href = link.getAttribute('href');
        
        if (href === currentPath || 
            (currentPath === '/' && href === '/about') ||
            (href !== '/' && currentPath.startsWith(href))) {
            
            link.classList.add('active');
            
            // 드롭다운 내부 항목인 경우 부모 드롭다운도 활성화
            const parentDropdown = link.closest('.dropdown');
            if (parentDropdown) {
                const dropdownToggle = parentDropdown.querySelector('.dropdown-toggle');
                if (dropdownToggle) {
                    dropdownToggle.classList.add('active');
                }
            }
        } else {
            link.classList.remove('active');
        }
    });
}
