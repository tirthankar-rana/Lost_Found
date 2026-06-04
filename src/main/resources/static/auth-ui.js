(function () {
    function updateAuthNav() {
        const userId = localStorage.getItem('lf_userId');
        const name = localStorage.getItem('lf_name');
        const navGuest = document.getElementById('navGuest');
        const navUser = document.getElementById('navUser');
        const welcomeText = document.getElementById('welcomeText');

        if (userId) {
            if (navGuest) navGuest.style.display = 'none';
            if (navUser) navUser.style.display = 'inline';
            if (welcomeText) welcomeText.textContent = `Hello, ${name || "User"} (#${userId})`;
        } else {
            if (navGuest) navGuest.style.display = 'inline';
            if (navUser) navUser.style.display = 'none';
            if (welcomeText) welcomeText.textContent = 'Not logged in';
        }
    }

    window.logoutLF = function () {
        localStorage.removeItem('lf_userId');
        localStorage.removeItem('lf_name');
        window.location.href = '/';
    };

    updateAuthNav();
})();
