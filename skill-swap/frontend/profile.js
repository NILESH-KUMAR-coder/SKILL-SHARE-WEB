// PROFILE PAGE
document.addEventListener("DOMContentLoaded", () => {
    // Apply saved theme from localStorage
    document.documentElement.setAttribute(
        "data-theme",
        localStorage.getItem("theme") || "light"
    );

    // Fetch and load the user profile
    loadProfilePage();
});

// Function to load the user profile
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
            avatar = `/uploads/${avatar}`;
        } else if (!avatar) {
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

// PROFILE EDIT
const editForm = document.getElementById("editProfileForm");
if (editForm) {
    // Fetch current profile data to pre-populate the fields
    apiFetch("/user/me").then(u => {
        document.getElementById("edit_name").value = u.name || "";
        document.getElementById("edit_displayName").value = u.displayName || "";
        document.getElementById("edit_contact").value = u.contact || "";
        document.getElementById("edit_bio").value = u.bio || "";
        const prev = u.profileImage ? `/uploads/${u.profileImage}` : "https://via.placeholder.com/96";
        document.getElementById("previewAvatar").src = prev;  // Image preview
    });

    // Handle profile edit form submission
    editForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const name = document.getElementById("edit_name").value;
        const displayName = document.getElementById("edit_displayName").value;
        const contact = document.getElementById("edit_contact").value;
        const bio = document.getElementById("edit_bio").value;
        let profileImage = document.getElementById("previewAvatar").src;

        try {
            const fileInput = document.getElementById("uploadProfileImage");
            if (fileInput && fileInput.files[0]) {
                // Upload the new image
                const uploadedImage = await uploadProfileImage(fileInput.files[0]);
                profileImage = uploadedImage;  // Use the URL of the uploaded image
            }
            // Sending the updated data to backend (including profile image if changed)
            const response = await apiFetch("/user/me", {
                method: "PUT",
                body: JSON.stringify({ name, displayName, contact, bio, profileImage })
            });

            alert("Profile updated.");
            window.location.href = "profile.html";  // Redirect to profile page
            loadProfilePage();  // Reload profile data
        } catch (err) {
            alert("Update failed: " + err.message);
        }
    });
}
