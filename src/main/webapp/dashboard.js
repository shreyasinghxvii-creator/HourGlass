/**
 * HourGlass Dashboard JavaScript
 * Handles filtering, searching, sorting, skill pills, and interactive request buttons.
 */
document.addEventListener("DOMContentLoaded", () => {
  const searchInput = document.querySelector(".search-input");
  const categoryChips = document.querySelectorAll(".category-chip");

  const filterGroups = document.querySelectorAll(
    ".secondary-filters-section .filter-group",
  );
  let modeFilterChips = [];
  let creditFilterChips = [];

  filterGroups.forEach((group) => {
    const label = group.querySelector(".filter-label");
    if (label) {
      const labelText = label.textContent.trim().toLowerCase();
      if (labelText.includes("mode")) {
        modeFilterChips = group.querySelectorAll(".filter-chip");
      } else if (labelText.includes("credit")) {
        creditFilterChips = group.querySelectorAll(".filter-chip");
      }
    }
  });

  const sortSelect = document.getElementById("sort-select");
  const resultsCountEl = document.querySelector(".results-count");
  const servicesGrid = document.querySelector(".services-grid");
  const serviceCards = Array.from(document.querySelectorAll(".service-card"));
  const skillPills = document.querySelectorAll(".skill-pill");

  let noResultsEl = document.querySelector(".empty-services-filtered");
  if (!noResultsEl && servicesGrid) {
    noResultsEl = document.createElement("div");
    noResultsEl.className = "empty-services empty-services-filtered";
    noResultsEl.style.display = "none";
    noResultsEl.innerHTML = `
            <h3>No matching services found</h3>
            <p>Try adjusting your search terms or filter criteria.</p>
        `;
    servicesGrid.appendChild(noResultsEl);
  }

  function updateServices() {
    const query = searchInput ? searchInput.value.toLowerCase().trim() : "";

    const activeCategoryChip = document.querySelector(".category-chip.active");
    const selectedCategory = activeCategoryChip
      ? activeCategoryChip.textContent.trim()
      : "All";

    const activeModeChip = modeFilterChips.length
      ? Array.from(modeFilterChips).find((chip) =>
          chip.classList.contains("active"),
        )
      : null;
    const selectedMode = activeModeChip
      ? activeModeChip.textContent.trim()
      : "All";

    const activeCreditChip = creditFilterChips.length
      ? Array.from(creditFilterChips).find((chip) =>
          chip.classList.contains("active"),
        )
      : null;
    const selectedCredit = activeCreditChip
      ? activeCreditChip.textContent.trim()
      : "All";

    let visibleCount = 0;

    serviceCards.forEach((card) => {
      const cardCategory = card.getAttribute("data-category") || "";
      const cardMode = card.getAttribute("data-mode") || "";
      const cardCreditsAttr = card.getAttribute("data-credits");

      const titleEl = card.querySelector(".service-title");
      const descEl = card.querySelector(".service-desc");
      const providerEl = card.querySelector(".provider-name");
      const tagsContainer = card.querySelector(".card-tags");

      const title = titleEl ? titleEl.textContent.toLowerCase() : "";
      const desc = descEl ? descEl.textContent.toLowerCase() : "";
      const provider = providerEl ? providerEl.textContent.toLowerCase() : "";
      const tagsText = tagsContainer
        ? tagsContainer.textContent.toLowerCase()
        : "";

      const matchesSearch =
        !query ||
        title.includes(query) ||
        desc.includes(query) ||
        provider.includes(query) ||
        cardCategory.toLowerCase().includes(query) ||
        tagsText.includes(query);

      const matchesCategory =
        selectedCategory === "All" ||
        cardCategory.toLowerCase() === selectedCategory.toLowerCase();

      const matchesMode =
        selectedMode === "All" ||
        cardMode.toLowerCase() === selectedMode.toLowerCase();

      let matchesCredit = true;
      const cost = cardCreditsAttr !== null ? parseInt(cardCreditsAttr, 10) : 0;
      if (selectedCredit === "Up to 1") {
        matchesCredit = cost <= 1;
      } else if (selectedCredit === "Up to 2") {
        matchesCredit = cost <= 2;
      } else if (selectedCredit === "Up to 3") {
        matchesCredit = cost <= 3;
      }

      if (matchesSearch && matchesCategory && matchesMode && matchesCredit) {
        card.style.display = "";
        visibleCount++;
      } else {
        card.style.display = "none";
      }
    });

    if (noResultsEl) {
      if (serviceCards.length > 0 && visibleCount === 0) {
        noResultsEl.style.display = "";
      } else {
        noResultsEl.style.display = "none";
      }
    }

    if (resultsCountEl) {
      resultsCountEl.textContent = `${visibleCount} ${
        visibleCount === 1 ? "service available" : "services available"
      }`;
    }
  }

  function sortServices() {
    if (!sortSelect || !servicesGrid) return;
    const sortValue = sortSelect.value;

    let sortedCards = [...serviceCards];

    if (sortValue === "recommended") {
      sortedCards.sort((a, b) => {
        const countA = parseInt(
          a.getAttribute("data-request-count") || "0",
          10,
        );
        const countB = parseInt(
          b.getAttribute("data-request-count") || "0",
          10,
        );
        if (countA !== countB) return countB - countA;

        const timeA = parseInt(a.getAttribute("data-created-at") || "0", 10);
        const timeB = parseInt(b.getAttribute("data-created-at") || "0", 10);
        if (timeA !== timeB) return timeB - timeA;

        const costA = parseInt(a.getAttribute("data-credits") || "0", 10);
        const costB = parseInt(b.getAttribute("data-credits") || "0", 10);
        return costA - costB;
      });
    } else if (sortValue === "newest") {
      sortedCards.sort((a, b) => {
        const timeA = parseInt(a.getAttribute("data-created-at") || "0", 10);
        const timeB = parseInt(b.getAttribute("data-created-at") || "0", 10);
        return timeB - timeA;
      });
    } else if (sortValue === "most_requested") {
      sortedCards.sort((a, b) => {
        const countA = parseInt(
          a.getAttribute("data-request-count") || "0",
          10,
        );
        const countB = parseInt(
          b.getAttribute("data-request-count") || "0",
          10,
        );
        if (countA !== countB) return countB - countA;

        const timeA = parseInt(a.getAttribute("data-created-at") || "0", 10);
        const timeB = parseInt(b.getAttribute("data-created-at") || "0", 10);
        return timeB - timeA;
      });
    }

    sortedCards.forEach((card) => {
      servicesGrid.appendChild(card);
    });
  }

  if (searchInput) {
    searchInput.addEventListener("input", () => {
      updateServices();
    });
  }

  categoryChips.forEach((chip) => {
    chip.addEventListener("click", () => {
      categoryChips.forEach((c) => c.classList.remove("active"));
      chip.classList.add("active");
      updateServices();
    });
  });

  modeFilterChips.forEach((chip) => {
    chip.addEventListener("click", () => {
      modeFilterChips.forEach((c) => c.classList.remove("active"));
      chip.classList.add("active");
      updateServices();
    });
  });

  creditFilterChips.forEach((chip) => {
    chip.addEventListener("click", () => {
      creditFilterChips.forEach((c) => c.classList.remove("active"));
      chip.classList.add("active");
      updateServices();
    });
  });

  if (sortSelect) {
    sortSelect.addEventListener("change", () => {
      sortServices();
      updateServices();
    });
  }

  skillPills.forEach((pill) => {
    pill.addEventListener("click", () => {
      const skillNameEl = pill.querySelector(".skill-name");
      if (skillNameEl && searchInput) {
        searchInput.value = skillNameEl.textContent.trim();
        updateServices();
        searchInput.scrollIntoView({ behavior: "smooth", block: "center" });
      }
    });
  });

  const requestButtons = document.querySelectorAll(
    ".btn-request:not(.offer-service-link)",
  );
  requestButtons.forEach((button) => {
    button.addEventListener("click", () => {
      const serviceId = button.getAttribute("data-service-id");

      if (!serviceId) {
        alert("Invalid service.");
        return;
      }

      const form = document.createElement("form");
      form.method = "POST";
      const basePath = window.contextPath || "";
      form.action = basePath + "/request-service";

      const input = document.createElement("input");
      input.type = "hidden";
      input.name = "serviceId";
      input.value = serviceId;

      form.appendChild(input);
      document.body.appendChild(form);
      form.submit();
    });
  });

  sortServices();
  updateServices();
});
