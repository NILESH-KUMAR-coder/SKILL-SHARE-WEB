// ------------------------------
// CONFIG
// ------------------------------
const API_BASE = "http://localhost:8080/api";
const TOKEN_KEY = "ss_token";
const USER_KEY = "ss_user";

// ------------------------------
// THEME SYSTEM (Light / Dark)
// ------------------------------
function applyTheme() {
    const theme = localStorage.getItem("theme") || "light";
    document.body.classList.toggle("dark", theme === "dark");
}

function toggleTheme() {
    const current = localStorage.getItem("theme") || "light";
    const next = current === "light" ? "dark" : "light";
    localStorage.setItem("theme", next);
    applyTheme();
}


// ------------------------------
// Helper: API Fetch with Token
// ------------------------------


async function apiFetch(path, options = {}) {
    const token = localStorage.getItem(TOKEN_KEY);

    options.headers = {
        "Content-Type": "application/json",
        ...(options.headers || {}),
        ...(token ? { "Authorization": "Bearer " + token } : {})
    };

    // allow passing full url
    const url = path.startsWith("http") ? path : (API_BASE + path);

    const res = await fetch(url, options);

    if (!res.ok) {
        let text = await res.text();
        try { text = JSON.parse(text); } catch (e) {}
        throw new Error(text.error || text || res.statusText);
    }

    // if no content
    if (res.status === 204) return null;
    const contentType = res.headers.get("Content-Type") || "";
    if (contentType.includes("application/json")) {
        return res.json();
    } else {
        return res.text();
    }
}

// ------------------------------
// AUTH (login/register)
// ------------------------------
async function handleRegister(e) {
    e && e.preventDefault();

    const username = document.getElementById("reg_username")?.value;
    const password = document.getElementById("reg_password")?.value;
    const name = document.getElementById("reg_name")?.value;
    const email = document.getElementById("reg_email")?.value;
    const displayName = document.getElementById("reg_display_name")?.value || "";
    const contact = document.getElementById("reg_contact")?.value || "";
    const bio = document.getElementById("reg_bio")?.value || "";

    try {
        const data = await apiFetch("/auth/register", {
            method: "POST",
            body: JSON.stringify({ username, password, name, email, displayName, contact, bio })
        });

        localStorage.setItem(TOKEN_KEY, data.token);
        localStorage.setItem(USER_KEY, data.username);
        alert("Registered successfully!");
        window.location.href = "dashboard.html";

    } catch (err) {
        alert("Registration failed: " + err.message);
    }
}

async function handleLogin(e) {
    e && e.preventDefault();

    const username = document.getElementById("login_username")?.value;
    const password = document.getElementById("login_password")?.value;

    try {
        const data = await apiFetch("/auth/login", {
            method: "POST",
            body: JSON.stringify({ username, password })
        });

        localStorage.setItem(TOKEN_KEY, data.token);
        localStorage.setItem(USER_KEY, data.username);
        alert("Login successful!");
        window.location.href = "dashboard.html";

    } catch (err) {
        alert("Login failed: " + err.message);
    }
}

// ------------------------------
// LOGOUT
// ------------------------------
function handleLogout() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    window.location.href = "index.html";
}

// ------------------------------
// LISTINGS CRUD
// ------------------------------
async function createListing() {
    const title = document.getElementById("title").value;
    const description = document.getElementById("description").value;
    const skillOffered = document.getElementById("skillOffered").value;
    const skillNeeded = document.getElementById("skillNeeded").value;
    const contact = document.getElementById("contact").value;

    if (!title || !skillOffered) {
        alert("Title and Offered Skill are required.");
        return;
    }

    try {
        const response = await apiFetch("/listings/create", {
            method: "POST",
            body: JSON.stringify({ title, description, skillOffered, skillNeeded, contact })
        });
        alert("Listing created!");
        loadAllListings();
        loadMyListings();
    } catch (err) {
        alert("Error creating listing: " + err.message);
    }
}

function renderListingTable(listing, includeOwner = true, showActions = false) {
    const owner = listing.ownerUsername || "Unknown";
    const id = listing.id;
    return `
        <table class="listing-table" border="0" cellpadding="0" cellspacing="0" style="margin-bottom:10px;">
            <tr><th colspan="2">${escapeHtml(listing.title)}</th></tr>
            <tr><td colspan="2">${escapeHtml(listing.description || "")}</td></tr>
            <tr><td><b>Offered:</b></td><td>${escapeHtml(listing.skillOffered)}</td></tr>
            <tr><td><b>Needed:</b></td><td>${escapeHtml(listing.skillNeeded || "")}</td></tr>
            ${includeOwner ? `<tr><td><b>Owner:</b></td><td>${escapeHtml(owner)}</td></tr>` : ""}
            <tr><td><b>Contact:</b></td><td>${escapeHtml(listing.contact || "")}</td></tr>
            ${showActions ? `<tr><td colspan="2">
              <button data-id="${id}" class="btn editListing">Edit</button>
              <button data-id="${id}" class="btn ghost deleteListing">Delete</button>
            </td></tr>` : ""}
        </table>
    `;
}

function escapeHtml(str = "") {
    return String(str)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;");
}

// Load all public listings
async function loadAllListings() {
    try {
        const listings = await apiFetch("/listings/all");
        const box = document.getElementById("allListings");
        if (!box) return;

        box.innerHTML = "";
        listings.forEach(l => {
            box.innerHTML += renderListingTable(l, true, false);
        });
    } catch (err) {
        console.error("Could not load listings:", err);
    }
}

// Load the current user's listings
async function loadMyListings() {
    try {
        const listings = await apiFetch("/listings/mine");
        const box = document.getElementById("myListings");
        if (!box) return;

        box.innerHTML = "";
        listings.forEach(l => {
            box.innerHTML += renderListingTable(l, false, true);
        });

        // attach edit/delete handlers
        document.querySelectorAll(".deleteListing").forEach(btn => {
            btn.addEventListener("click", async (ev) => {
                const id = ev.target.dataset.id;
                if (!confirm("Delete this listing?")) return;
                try {
                    await apiFetch(`/listings/${id}`, { method: "DELETE" });
                    loadMyListings();
                } catch (e) { alert("Delete failed: " + e.message); }
            });
        });

        document.querySelectorAll(".editListing").forEach(btn => {
            btn.addEventListener("click", async (ev) => {
                const id = ev.target.dataset.id;
                // simple inline prompt edit for demo
                const newTitle = prompt("New title:");
                if (newTitle === null) return;
                try {
                    await apiFetch(`/listings/${id}`, {
                        method: "PUT",
                        body: JSON.stringify({ title: newTitle })
                    });
                    loadMyListings();
                } catch (e) { alert("Update failed: " + e.message); }
            });
        });

    } catch (err) {
        console.error("Could not load your listings:", err);
    }
}

async function contactOwner(owner, listingId) {
    try {
        await apiFetch(`/contact/send/${owner}/${listingId}`, {
            method: "POST",
        });
        alert("Request sent!");
    } catch (e) {
        alert("Failed: " + e.message);
    }
}

async function loadIncomingRequests() {
    try {
        const data = await apiFetch("/contact/incoming");
        const box = document.getElementById("incomingRequests");
        if (!box) return;

        box.innerHTML = "";

        if (data.length === 0) {
            box.innerHTML = "<p>No contact requests.</p>";
            return;
        }

        data.forEach(r => {
            box.innerHTML += `
                <div class="card" style="margin-bottom:10px; padding:10px;">
                    <p><b>From:</b> ${escapeHtml(r.senderUsername)}</p>
                    <p><b>Listing ID:</b> ${r.listingId}</p>
                    <p><b>Status:</b> ${r.status}</p>
                    ${r.status === "pending" ? `
                        <button class="btn" onclick="acceptRequest(${r.id})">Accept</button>
                        <button class="btn ghost" onclick="declineRequest(${r.id})">Decline</button>
                    ` : ""}
                </div>
            `;
        });

    } catch (err) {
        console.error("Could not load contact requests:", err);
    }
}

async function acceptRequest(id) {
    try {
        await apiFetch(`/contact/accept/${id}`, { method: "POST" });
        alert("Request accepted!");
        loadIncomingRequests();
    } catch (e) {
        alert("Failed: " + e.message);
    }
}

async function declineRequest(id) {
    try {
        await apiFetch(`/contact/decline/${id}`, { method: "POST" });
        alert("Request declined.");
        loadIncomingRequests();
    } catch (e) {
        alert("Failed: " + e.message);
    }
}


// Find matches by skill (excluding own listings)
async function findMatches() {
    const skill = document.getElementById("matchSkill")?.value.trim();
    if (!skill) {
        alert("Enter a skill to find matches.");
        return;
    }

    try {
        const matches = await apiFetch(`/listings/match?skill=${encodeURIComponent(skill)}`);
        const box = document.getElementById("matches");
        if (!box) return;

        box.innerHTML = "";
        if (!matches || matches.length === 0) {
            box.innerHTML = "<p>No matches found.</p>";
            return;
        }

        matches.forEach(m => {
            if (m.ownerUsername === localStorage.getItem(USER_KEY)) return;

            box.innerHTML += `
                <div class="card" style="margin-bottom:15px;">
                    ${renderListingTable(m, true)}
                    <button class="btn" onclick="contactOwner('${m.ownerUsername}', ${m.id})">
                        Contact Owner
                    </button>
                </div>
             `;
        });
    } catch (err) {
        alert("Failed to find matches: " + err.message);
    }
}

async function loadAllowedChats() {
    try {
        const allowed = await apiFetch("/contact/allowed");
        const box = document.getElementById("chatUsers");
        if (!box) return;

        box.innerHTML = "";
        allowed.forEach(u => {
            box.innerHTML += `
                <div class="chat-user" onclick="openChat('${u}')">
                    ${u}
                </div>
            `;
        });
    } catch (e) {
        console.error("Failed to load chats:", e);
    }
}


// ------------------------------
// PROFILE fetch for pages
// ------------------------------
async function loadProfilePage() {
    try {
        // Fetch user profile data from the API
        const profile = await apiFetch("/user/me");
        console.log(profile); // For debugging, to check what profile data is returned

        // Ensure all profile fields are updated correctly
        document.getElementById("profileName").textContent = profile.name || "No name provided";  // Ensure a fallback if no name
        document.getElementById("profileDisplay").textContent = profile.displayName || "No display name"; // Ensure a fallback
        document.getElementById("profileContact").textContent = profile.contact || "No contact provided"; // Ensure a fallback
        document.getElementById("profileBio").textContent = profile.bio || "No bio provided";  // Ensure a fallback

        // Handle profile image
        let avatar = profile.profileImage;
        if (avatar && !avatar.startsWith('http')) {
            avatar = `/uploads/${avatar}`;  // Only prepend /uploads if it's not a full URL
        }else if (!avatar) {
            avatar = "https://via.placeholder.com/96"; // Fallback for no image
        }
        document.getElementById("profileAvatar").src = avatar; // Update the avatar

        // Load user's listings (optional)
        const listings = await apiFetch("/listings/mine");
        const box = document.getElementById("profileListings");
        if (box) {
            box.innerHTML = "";  // Clear current listings
            listings.forEach(l => box.innerHTML += renderListingTable(l, false, false)); // Assuming renderListingTable is defined to render the listings
        }
    } catch (err) {
        console.error("Error loading profile:", err);
    }
}


// ------------------------------
// FILE UPLOAD (profile image)
// ------------------------------
async function uploadProfileImage(file) {
    const token = localStorage.getItem(TOKEN_KEY);
    const form = new FormData();
    form.append("file", file);

    try {
        const res = await fetch(API_BASE + "/user/me/upload", {
            method: "POST",
            headers: {
                ...(token ? { "Authorization": "Bearer " + token } : {})
            },
            body: form
        });

        if (!res.ok) {
            const txt = await res.text();
            throw new Error(txt || res.statusText);
        }

        const data = await res.json();  // Get the response after upload
        console.log(data);

        // Get the uploaded image URL from the server response
        let imageUrl = data.url;

        // If the URL doesn't already start with 'http://' or 'https://', assume it's a relative URL and prepend the base URL
        if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            imageUrl = `${window.location.origin}${imageUrl}`; // Add the base URL
        }

        return imageUrl;  // Return the correctly formatted URL
    } catch (err) {
        alert("Image upload failed: " + err.message);
    }
}


// ------------------------------
// CHAT HELPER
// ------------------------------
function openChat(username) {
    window.location.href = "chat.html";
}

// ------------------------------
// CHAT FUNCTIONS (NEW)
// ------------------------------

// ------------------------------
// DOM Event Listeners
// ------------------------------
document.addEventListener("DOMContentLoaded", () => {

    // ---------------------
    // THEME SETUP
    // ---------------------
    applyTheme();

    const themeBtn = document.getElementById("themeToggle");
    if (themeBtn) themeBtn.addEventListener("click", toggleTheme);


    // ---------------------
    // CHAT PAGE INITIALIZATION
    // ---------------------
    if (window.location.pathname.endsWith("chat.html")) {

        // Load accepted contacts (correct source of chat partners)
        loadAllowedContacts();

        // Send button
        const btn = document.getElementById("btnSend");
        if (btn) btn.addEventListener("click", sendChatMessage);

        // Press Enter to send
        const input = document.getElementById("chatMessage");
        if (input) {
            input.addEventListener("keypress", (e) => {
                if (e.key === "Enter") sendChatMessage();
            });
        }
    }


    // ---------------------
    // LOGOUT BUTTON
    // ---------------------
    const logout = document.getElementById("btnLogout");
    if (logout) logout.addEventListener("click", handleLogout);

    
    // ---------------------
    // DASHBOARD ACTIONS
    // ---------------------
    if (document.getElementById("incomingRequests")) loadIncomingRequests();

    const btnMatch = document.getElementById("btnMatch");
    if (btnMatch) btnMatch.addEventListener("click", findMatches);

    if (document.getElementById("allListings")) loadAllListings();
    if (document.getElementById("myListings")) loadMyListings();

    const btnRefresh = document.getElementById("btnRefresh");
    if (btnRefresh) btnRefresh.addEventListener("click", () => {
        loadMyListings();
        loadAllListings();
    });


    // ---------------------
    // LOGIN / REGISTER FORMS
    // ---------------------
    const loginForm = document.getElementById("loginForm");
    const registerForm = document.getElementById("registerForm");

    if (loginForm) loginForm.addEventListener("submit", handleLogin);
    if (registerForm) registerForm.addEventListener("submit", handleRegister);

    // ---------------------
    // CREATE LISTING FORM
    // ---------------------
    document.getElementById("createListingForm").addEventListener("submit", function (e) {
        e.preventDefault();  // Prevent default form submission
        createListing();     // Call the function to handle the submission
    });


    // ---------------------
    // PROFILE PAGE
    // ---------------------
    if (document.getElementById("profileName")) loadProfilePage();

    // PROFILE EDIT
    const editForm = document.getElementById("editProfileForm");
    if (editForm) {
        apiFetch("/user/me").then(u => {
            document.getElementById("edit_name").value = u.name || "";
            document.getElementById("edit_displayName").value = u.displayName || "";
            document.getElementById("edit_contact").value = u.contact || "";
            document.getElementById("edit_bio").value = u.bio || "";
            const prev = u.profileImage ? `/uploads/${u.profileImage}` : "https://via.placeholder.com/96";
            document.getElementById("previewAvatar").src = prev;
        });

        editForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            const name = document.getElementById("edit_name").value;
            const displayName = document.getElementById("edit_displayName").value;
            const contact = document.getElementById("edit_contact").value;
            const bio = document.getElementById("edit_bio").value;
            const profileImage = document.getElementById("previewAvatar").src;

            try {
                // If a new profile image is selected, upload it first
                if (profileImage && profileImage !== "https://via.placeholder.com/96") {
                    const fileInput = document.getElementById("uploadProfileImage"); // Assuming the file input is there
                    if (fileInput && fileInput.files[0]) {
                        const uploadedImage = await uploadProfileImage(fileInput.files[0]);
                        profileImage = uploadedImage.url; // Use the URL of the uploaded image
                    }
                }
                const response = await apiFetch("/user/me", {
                    method: "PUT",
                    body: JSON.stringify({ name, displayName, contact, bio , profileImage })
                });
                console.log(response);
                alert("Profile updated.");
                window.location.href = "profile.html";
                loadProfilePage();
            } catch (err) {
                alert("Update failed: " + err.message);
            }
        });
    }
});
