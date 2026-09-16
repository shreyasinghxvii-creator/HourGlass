/**
 * HourGlass Incoming Requests JavaScript
 * Controls request submission interactions, disabled state guards,
 * and QR scanning verification.
 */

document.addEventListener("DOMContentLoaded", () => {
  // --------------------------------------------------
  // 1. Existing Action Forms Handling
  // Accept / Reject / Complete
  // --------------------------------------------------

  const actionForms = document.querySelectorAll(".action-form");

  actionForms.forEach((form) => {
    form.addEventListener("submit", (e) => {
      const isRejectForm = form.classList.contains("form-reject");

      if (isRejectForm) {
        const confirmed = confirm(
          "Are you sure you want to decline this service request?",
        );

        if (!confirmed) {
          e.preventDefault();
          return;
        }
      }

      // Disable all action buttons in the current card
      // to prevent double submission.
      const parentCard = form.closest(".request-card");

      if (parentCard) {
        const buttons = parentCard.querySelectorAll(".btn-action");

        buttons.forEach((btn) => {
          btn.disabled = true;
        });
      }

      const submitBtn = form.querySelector("button[type='submit']");

      if (submitBtn) {
        submitBtn.textContent = isRejectForm ? "Declining..." : "Processing...";
      }
    });
  });

  // --------------------------------------------------
  // 2. QR Scanner Modal & Processing Logic
  // --------------------------------------------------

  const modal = document.getElementById("qr-modal");
  const closeBtn = document.getElementById("qr-modal-close-btn");
  const rescanBtn = document.getElementById("qr-rescan-btn");
  const doneBtn = document.getElementById("qr-done-btn");
  const statusBox = document.getElementById("qr-status-box");
  const statusText = statusBox
    ? statusBox.querySelector(".qr-status-text")
    : null;

  let html5QrcodeScanner = null;

  // Prevent multiple QR results from being processed.
  let isProcessingScan = false;

  // Prevent overlapping start/stop/clear operations.
  let isTransitioning = false;

  // Request currently being verified.
  let activeRequestId = null;

  // Reload page after successful verification
  // when the modal is closed.
  let reloadOnClose = false;

  // --------------------------------------------------
  // 3. Scan QR Button
  // --------------------------------------------------

  const scanButtons = document.querySelectorAll(".scan-qr-btn");

  scanButtons.forEach((button) => {
    button.addEventListener("click", () => {
      const reqIdStr = button.getAttribute("data-request-id");

      if (!reqIdStr) {
        return;
      }

      const parsedRequestId = parseInt(reqIdStr, 10);

      if (isNaN(parsedRequestId) || parsedRequestId <= 0) {
        return;
      }

      activeRequestId = parsedRequestId;

      openQrModal();
    });
  });

  // --------------------------------------------------
  // 4. Modal Controls
  // --------------------------------------------------

  if (closeBtn) {
    closeBtn.addEventListener("click", () => {
      closeQrModal();
    });
  }

  if (doneBtn) {
    doneBtn.addEventListener("click", () => {
      closeQrModal();
    });
  }

  if (rescanBtn) {
    rescanBtn.addEventListener("click", async () => {
      if (isTransitioning) {
        return;
      }

      resetScannerState();

      // Make absolutely sure any previous scanner
      // has been stopped and cleared before restarting.
      await stopAndClearScanner();

      await startCamera();
    });
  }

  // --------------------------------------------------
  // 5. Escape Key Accessibility
  // --------------------------------------------------

  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape" && modal && modal.classList.contains("active")) {
      closeQrModal();
    }
  });

  // --------------------------------------------------
  // 6. Open QR Modal
  // --------------------------------------------------

  async function openQrModal() {
    if (!modal || isTransitioning) {
      return;
    }

    reloadOnClose = false;
    isProcessingScan = false;

    activeRequestId = activeRequestId;

    modal.classList.add("active");
    modal.setAttribute("aria-hidden", "false");

    resetScannerState();

    // Ensure no old scanner instance remains.
    await stopAndClearScanner();

    // Start a fresh camera session.
    await startCamera();
  }

  // --------------------------------------------------
  // 7. Close QR Modal
  // --------------------------------------------------

  async function closeQrModal() {
    if (!modal || isTransitioning) {
      return;
    }

    await stopAndClearScanner();

    modal.classList.remove("active");
    modal.setAttribute("aria-hidden", "true");

    activeRequestId = null;
    isProcessingScan = false;

    if (reloadOnClose) {
      window.location.reload();
    }
  }

  // --------------------------------------------------
  // 8. Update Status Message
  // --------------------------------------------------

  function updateStatus(message, stateClass) {
    if (!statusBox || !statusText) {
      return;
    }

    statusBox.className = "qr-status-box " + (stateClass || "");

    statusText.textContent = message;
  }

  // --------------------------------------------------
  // 9. Reset Scanner UI
  // --------------------------------------------------

  function resetScannerState() {
    isProcessingScan = false;

    if (rescanBtn) {
      rescanBtn.classList.add("hidden");
    }

    if (doneBtn) {
      doneBtn.classList.add("hidden");
    }

    updateStatus(
      "Align the QR code within the frame to verify completion.",
      "",
    );
  }

  // --------------------------------------------------
  // 10. Stop and Clear Scanner
  // --------------------------------------------------
  //
  // We intentionally do NOT depend on undocumented
  // numeric scanner state values.
  //
  // stop() is awaited before clear() so that the camera
  // lifecycle is deterministic.
  // --------------------------------------------------

  async function stopAndClearScanner() {
    if (!html5QrcodeScanner) {
      return;
    }

    isTransitioning = true;

    const scanner = html5QrcodeScanner;

    try {
      // Try to stop the active camera/scanner.
      // If it was already stopped or never started,
      // the exception is safely ignored.
      try {
        await scanner.stop();
      } catch (stopError) {
        // Scanner was already stopped or not running.
      }

      // Clear the scanner UI only after stop() has
      // completed.
      try {
        scanner.clear();
      } catch (clearError) {
        // Ignore cleanup exceptions.
      }
    } finally {
      // Release our reference so the next scan
      // creates a completely fresh scanner instance.
      if (html5QrcodeScanner === scanner) {
        html5QrcodeScanner = null;
      }

      isTransitioning = false;
    }
  }

  // --------------------------------------------------
  // 11. Start Camera
  // --------------------------------------------------

  async function startCamera() {
    if (isTransitioning) {
      return;
    }

    if (typeof Html5Qrcode === "undefined") {
      updateStatus(
        "QR scanner library failed to load. Check your network connection.",
        "error",
      );

      return;
    }

    isTransitioning = true;

    try {
      // Always create a fresh scanner instance.
      if (!html5QrcodeScanner) {
        html5QrcodeScanner = new Html5Qrcode("qr-reader");
      }

      const config = {
        fps: 10,
        qrbox: {
          width: 220,
          height: 220,
        },
      };

      await html5QrcodeScanner.start(
        {
          facingMode: "environment",
        },
        config,
        onScanSuccess,
        onScanError,
      );
    } catch (err) {
      updateStatus(
        "Camera access was denied. Please allow camera access in your browser settings and try again.",
        "error",
      );

      // Clean up failed scanner initialization.
      if (html5QrcodeScanner) {
        const failedScanner = html5QrcodeScanner;

        try {
          await failedScanner.clear();
        } catch (clearError) {
          // Ignore cleanup exceptions.
        }

        if (html5QrcodeScanner === failedScanner) {
          html5QrcodeScanner = null;
        }
      }
    } finally {
      isTransitioning = false;
    }
  }

  // --------------------------------------------------
  // 12. QR Scan Success
  // --------------------------------------------------

  async function onScanSuccess(decodedText) {
    // Ignore additional frames while processing.
    if (isProcessingScan || isTransitioning) {
      return;
    }

    isProcessingScan = true;

    let payload;

    // ----------------------------------------------
    // Parse JSON payload
    // ----------------------------------------------

    try {
      payload = JSON.parse(decodedText);
    } catch (e) {
      updateStatus("Invalid QR code payload format.", "error");

      await stopAndClearScanner();

      if (rescanBtn) {
        rescanBtn.classList.remove("hidden");
      }

      return;
    }

    // ----------------------------------------------
    // Extract requestId and token
    // ----------------------------------------------

    const qrRequestId = payload ? payload.requestId : null;

    const qrToken = payload ? payload.token : null;

    // ----------------------------------------------
    // Validate payload structure
    // ----------------------------------------------

    if (
      !qrRequestId ||
      typeof qrRequestId !== "number" ||
      qrRequestId <= 0 ||
      !qrToken ||
      typeof qrToken !== "string" ||
      qrToken.trim() === ""
    ) {
      updateStatus("Invalid QR code data structure.", "error");

      await stopAndClearScanner();

      if (rescanBtn) {
        rescanBtn.classList.remove("hidden");
      }

      return;
    }

    // ----------------------------------------------
    // Make sure QR belongs to this request
    // ----------------------------------------------

    if (qrRequestId !== activeRequestId) {
      updateStatus(
        "This QR code belongs to a different service request.",
        "error",
      );

      await stopAndClearScanner();

      if (rescanBtn) {
        rescanBtn.classList.remove("hidden");
      }

      return;
    }

    // ----------------------------------------------
    // Stop camera BEFORE sending verification
    // ----------------------------------------------

    await stopAndClearScanner();

    updateStatus("Verifying QR token...", "");

    // ----------------------------------------------
    // Send verification request to backend
    // ----------------------------------------------

    submitVerification(qrRequestId, qrToken.trim());
  }

  // --------------------------------------------------
  // 13. QR Scan Error
  // --------------------------------------------------
  //
  // html5-qrcode calls this continuously when it does
  // not detect a QR code in a frame.
  // We intentionally suppress this normal noise.
  // --------------------------------------------------

  function onScanError(errorMessage) {
    // Normal frame-by-frame scanning noise.
  }

  // --------------------------------------------------
  // 14. Submit QR Verification
  // --------------------------------------------------

  function submitVerification(requestId, token) {
    const contextPath = window.CONTEXT_PATH || "";

    const endpoint = contextPath + "/scan-qr";

    // Send only requestId and token.
    // Provider identity must come from the
    // authenticated server-side session.
    const params = new URLSearchParams();

    params.append("requestId", requestId.toString());

    params.append("token", token);

    fetch(endpoint, {
      method: "POST",

      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },

      body: params.toString(),
    })
      // ----------------------------------------------
      // Parse server response
      // ----------------------------------------------

      .then((response) => {
        return response.json().then((data) => ({
          status: response.status,
          data: data,
        }));
      })

      // ----------------------------------------------
      // Handle result
      // ----------------------------------------------

      .then((result) => {
        const body = result.data;

        if (body && body.success === true) {
          reloadOnClose = true;

          updateStatus(
            body.message ||
              "Session verified successfully. Time credits transferred to your account.",
            "success",
          );

          if (doneBtn) {
            doneBtn.classList.remove("hidden");
          }

          return;
        }

        // ------------------------------------------
        // Verification failed
        // ------------------------------------------

        const errorMsg =
          body && body.message
            ? body.message
            : "Verification failed. Please try again.";

        updateStatus(errorMsg, "error");

        if (rescanBtn) {
          rescanBtn.classList.remove("hidden");
        }
      })

      // ----------------------------------------------
      // Network / unexpected error
      // ----------------------------------------------

      .catch(() => {
        updateStatus("Network or server error during verification.", "error");

        if (rescanBtn) {
          rescanBtn.classList.remove("hidden");
        }
      });
  }
});
