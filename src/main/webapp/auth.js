document.addEventListener('DOMContentLoaded', function () {
    const authContainer = document.getElementById('authContainer');
    const toRegisterBtn = document.getElementById('to-register');
    const toLoginBtn = document.getElementById('to-login');

    if (!authContainer) return;

    // Helper function to resolve current application context path
    function getContextPath() {
        const path = window.location.pathname;
        if (path.includes('/login')) {
            return path.substring(0, path.indexOf('/login'));
        }
        if (path.includes('/register')) {
            return path.substring(0, path.indexOf('/register'));
        }
        if (path.includes('/auth.jsp')) {
            return path.substring(0, path.indexOf('/auth.jsp'));
        }
        return '';
    }

    const contextPath = getContextPath();

    // Toggle panel UI and sync browser history URL
    function setMode(mode, updateHistory) {
        if (mode === 'register') {
            authContainer.classList.add('register-mode', 'right-panel-active');
            if (updateHistory) {
                const targetUrl = contextPath + '/register';
                if (window.location.pathname !== targetUrl) {
                    history.pushState({ mode: 'register' }, '', targetUrl);
                }
            }
        } else {
            authContainer.classList.remove('register-mode', 'right-panel-active');
            if (updateHistory) {
                const targetUrl = contextPath + '/login';
                if (window.location.pathname !== targetUrl) {
                    history.pushState({ mode: 'login' }, '', targetUrl);
                }
            }
        }
    }

    // 1. Initial State Initialization from JSP attribute
    const initialMode = authContainer.getAttribute('data-initial-mode') || 'login';
    setMode(initialMode, false);

    // 2. Event Listeners for Toggle Links
    if (toRegisterBtn) {
        toRegisterBtn.addEventListener('click', function (e) {
            e.preventDefault();
            setMode('register', true);
        });
    }

    if (toLoginBtn) {
        toLoginBtn.addEventListener('click', function (e) {
            e.preventDefault();
            setMode('login', true);
        });
    }

    // 3. Browser Back/Forward Popstate Handler
    window.addEventListener('popstate', function (e) {
        if (e.state && e.state.mode) {
            setMode(e.state.mode, false);
        } else {
            const currentPath = window.location.pathname;
            if (currentPath.endsWith('/register')) {
                setMode('register', false);
            } else {
                setMode('login', false);
            }
        }
    });
});