/**
 * HourGlass Offer Service JavaScript
 * Handles dynamic location field visibility based on Mode and client-side validation.
 */
document.addEventListener("DOMContentLoaded", () => {
    const modeSelect = document.getElementById("mode");
    const locationGroup = document.getElementById("locationGroup");
    const locationInput = document.getElementById("location");
    const offerForm = document.getElementById("offerServiceForm");

    function handleModeChange() {
        if (!modeSelect || !locationGroup || !locationInput) return;
        const selectedMode = modeSelect.value;

        if (selectedMode === "Offline") {
            locationGroup.style.display = "flex";
            locationInput.required = true;
        } else if (selectedMode === "Online") {
            locationGroup.style.display = "none";
            locationInput.required = false;
            locationInput.value = "";
        } else {
            // Default when unselected
            locationGroup.style.display = "flex";
            locationInput.required = false;
        }
    }

    if (modeSelect) {
        modeSelect.addEventListener("change", handleModeChange);
        // Initial state check
        handleModeChange();
    }

    if (offerForm) {
        offerForm.addEventListener("submit", (e) => {
            const title = document.getElementById("title");
            const description = document.getElementById("description");
            const category = document.getElementById("category");
            const mode = document.getElementById("mode");

            if (!title || !description || !category || !mode) return;

            if (!title.value.trim() || !description.value.trim() || !category.value || !mode.value) {
                e.preventDefault();
                alert("Please fill in all required fields.");
            }
        });
    }
});