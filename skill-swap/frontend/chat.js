let activeConversation = null;
let chatPollInterval = null;

// ------------------------------------
// Load list of recent conversations
// ------------------------------------

// chat.js
async function loadAllowedContacts() {
    const allowed = await apiFetch("/contact/allowed");
    const box = document.getElementById("conversations");
    box.innerHTML = "";

    allowed.forEach(user => {
        const el = document.createElement("div");
        el.className = "row";
        el.innerHTML = `<div style="flex:1">${user}</div>`;
        el.onclick = () => openConversation(user);
        box.appendChild(el);
    });
}

// ------------------------------------
// Open a conversation
// ------------------------------------
async function openConversation(withUser) {
    activeConversation = withUser;

    const win = document.getElementById("chatWindow");
    if (win) win.innerHTML = "";

    await loadConversation(withUser);

    if (chatPollInterval) clearInterval(chatPollInterval);
    chatPollInterval = setInterval(() => loadConversation(withUser), 2000);
}

// ------------------------------------
// Load messages in a conversation
// ------------------------------------
async function loadConversation(withUser) {
    if (!withUser) return;

    try {
        const messages = await apiFetch(`/messages/conversation?with=${encodeURIComponent(withUser)}`);
        const win = document.getElementById("chatWindow");
        if (!win) return;

        win.innerHTML = "";

        messages.forEach(m => {
            const mine = m.fromUsername === localStorage.getItem("ss_user");

            const div = document.createElement("div");
            div.className = `chat-message ${mine ? "chat-to" : "chat-from"}`;

            div.innerHTML = `
                <div class="chat-message-text">${escapeHtml(m.body)}</div>
                <div class="chat-meta">${mine ? "You" : m.fromUsername} · 
                    ${new Date(m.createdAt).toLocaleTimeString()}
                </div>
            `;

            win.appendChild(div);
        });

        win.scrollTop = win.scrollHeight;
    } catch (err) {
        console.error("Failed loading conversation", err);
    }
}


// ------------------------------------
// Send message
// ------------------------------------
async function sendChatMessage() {
    const input = document.getElementById("chatMessage");
    if (!input) return;

    const text = input.value.trim();
    if (!text || !activeConversation) return;

    try {
        await apiFetch("/messages/send", {
            method: "POST",
            body: JSON.stringify({
                toUsername: activeConversation,
                body: text   // <-- FIXED HERE
            })
        });

        input.value = "";
        loadConversation(activeConversation);

    } catch (err) {
        alert("Send failed: " + err.message);
    }
}


// ------------------------------------
// DOM READY
// ------------------------------------
document.addEventListener("DOMContentLoaded", () => {
    // theme
    const savedTheme = localStorage.getItem("theme") || "light";
    document.documentElement.setAttribute("data-theme", savedTheme);

    const themeBtn = document.getElementById("themeToggle");
    if (themeBtn) {
        updateThemeIcon(savedTheme);
        themeBtn.onclick = () => {
            const now = document.documentElement.getAttribute("data-theme");
            const next = now === "light" ? "dark" : "light";
            document.documentElement.setAttribute("data-theme", next);
            localStorage.setItem("theme", next);
            updateThemeIcon(next);
        };
    }

    function updateThemeIcon(mode) {
        if (!themeBtn) return;
        themeBtn.textContent = mode === "light" ? "🌙" : "☀️";
    }

    // -----------------------------
    // **THE FIXED PART**
    // -----------------------------
    // Load accepted contacts ONLY
    loadAllowedContacts();

    // Remove these broken calls:
    // loadConversations();
    // if (document.getElementById("conversations")) {
    //     loadConversations();
    //     setInterval(loadConversations, 5000);
    // }

    // -----------------------------
    // message send
    // -----------------------------
    const btn = document.getElementById("btnSend");
    if (btn) btn.addEventListener("click", sendChatMessage);

    // logout
    const logout = document.getElementById("btnLogout");
    if (logout) logout.addEventListener("click", handleLogout);
});
