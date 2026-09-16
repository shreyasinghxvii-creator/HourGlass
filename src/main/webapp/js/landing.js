/**
 * HOURGLASS — PRODUCT INTERACTIVE CONTROLLER (POLISHED PASS)
 * Features: Restrained 3D Tilt (5-7°), Smooth Sand Physics, Scroll-Reveal Observer, Time Credit Calculator
 */

document.addEventListener("DOMContentLoaded", () => {
  initScrollHeader();
  initHourglass3DTilt();
  initSandPhysicsEngine();
  initScrollReveal();
  initCreditSlider();
});

/* 1. SCROLL HEADER SHRINK STATE */
function initScrollHeader() {
  const navbar = document.getElementById("navbar");
  if (!navbar) return;

  window.addEventListener(
    "scroll",
    () => {
      if (window.scrollY > 30) {
        navbar.classList.add("scrolled");
      } else {
        navbar.classList.remove("scrolled");
      }
    },
    { passive: true },
  );
}

/* 2. RESTRAINED 3D TILT PARALLAX (MAX 6 DEGREES) */
function initHourglass3DTilt() {
  const viewport = document.getElementById("hourglass-viewport");
  const stage = document.getElementById("hourglass-stage");

  if (!viewport || !stage) return;

  let targetRotateX = 0;
  let targetRotateY = 0;
  let currentRotateX = 0;
  let currentRotateY = 0;

  window.addEventListener(
    "mousemove",
    (e) => {
      const rect = viewport.getBoundingClientRect();
      const centerX = rect.left + rect.width / 2;
      const centerY = rect.top + rect.height / 2;

      const offsetX = (e.clientX - centerX) / (window.innerWidth / 2);
      const offsetY = (e.clientY - centerY) / (window.innerHeight / 2);

      // Strictly limited tilt to 6 degrees for subtle weight
      targetRotateY = Math.max(-6, Math.min(6, offsetX * 6));
      targetRotateX = Math.max(-6, Math.min(6, -offsetY * 6));
    },
    { passive: true },
  );

  // Smooth Interpolated Lerp Loop
  function renderTilt() {
    currentRotateX += (targetRotateX - currentRotateX) * 0.08;
    currentRotateY += (targetRotateY - currentRotateY) * 0.08;

    stage.style.transform = `rotateX(${currentRotateX.toFixed(2)}deg) rotateY(${currentRotateY.toFixed(2)}deg)`;
    requestAnimationFrame(renderTilt);
  }

  renderTilt();
}

/* 3. PHYSICAL SAND FLOW ENGINE */
function initSandPhysicsEngine() {
  const canvas = document.getElementById("sand-canvas");
  if (!canvas) return;

  const ctx = canvas.getContext("2d");
  const width = (canvas.width = 220);
  const height = (canvas.height = 348);

  const centerX = width / 2;
  const neckY = height / 2;

  const grains = [];
  const maxGrains = 45;
  let bottomPileHeight = 0;

  function renderSand() {
    ctx.clearRect(0, 0, width, height);

    // 1. Upper Chamber Sand Body
    ctx.fillStyle = "#FFAA00";
    ctx.beginPath();
    ctx.moveTo(25, 30);
    ctx.lineTo(195, 30);
    ctx.lineTo(120, neckY - 10);
    ctx.lineTo(100, neckY - 10);
    ctx.closePath();
    ctx.fill();

    // 2. Continuous Fluid Sand Stream
    ctx.fillStyle = "#FFAA00";
    ctx.fillRect(
      centerX - 1.5,
      neckY - 10,
      3,
      height - neckY - 35 - bottomPileHeight,
    );

    // 3. Dynamic Falling Particles
    if (grains.length < maxGrains) {
      grains.push({
        x: centerX + (Math.random() * 3 - 1.5),
        y: neckY + Math.random() * 15,
        vy: 2.2 + Math.random() * 2.2,
        radius: Math.random() * 1.1 + 0.8,
      });
    }

    grains.forEach((grain) => {
      grain.y += grain.vy;

      ctx.beginPath();
      ctx.arc(grain.x, grain.y, grain.radius, 0, Math.PI * 2);
      ctx.fillStyle = "#FFAA00";
      ctx.fill();

      // Reset when hitting lower heap surface
      if (grain.y >= height - 30 - bottomPileHeight) {
        grain.y = neckY;
        grain.x = centerX + (Math.random() * 3 - 1.5);
        if (bottomPileHeight < 75) {
          bottomPileHeight += 0.018;
        }
      }
    });

    // 4. Accumulated Lower Chamber Heap Curve
    ctx.beginPath();
    ctx.moveTo(20, height - 20);
    ctx.quadraticCurveTo(
      centerX,
      height - 20 - bottomPileHeight * 1.25,
      width - 20,
      height - 20,
    );
    ctx.lineTo(width - 20, height - 20);
    ctx.lineTo(20, height - 20);
    ctx.closePath();
    ctx.fillStyle = "#FFAA00";
    ctx.fill();

    requestAnimationFrame(renderSand);
  }

  renderSand();
}

/* 4. SCROLL-REVEAL INTERSECTION OBSERVER */
function initScrollReveal() {
  const revealElements = document.querySelectorAll(".scroll-reveal");
  if (!revealElements.length) return;

  const observerOptions = {
    threshold: 0.15,
    rootMargin: "0px 0px -50px 0px",
  };

  const observer = new IntersectionObserver((entries, obs) => {
    entries.forEach((entry) => {
      if (entry.isIntersecting) {
        entry.target.classList.add("visible");
        obs.unobserve(entry.target);
      }
    });
  }, observerOptions);

  revealElements.forEach((el) => observer.observe(el));
}

/* 5. TIME CREDIT CALCULATOR SLIDER */
function initCreditSlider() {
  const slider = document.getElementById("time-credit-slider");
  const hoursVal = document.getElementById("slider-hours-val");
  const hoursLabel = document.getElementById("slider-hours-label");
  const creditsVal = document.getElementById("slider-credits-val");
  const creditsLabel = document.getElementById("slider-credits-label");

  if (!slider || !hoursVal || !hoursLabel || !creditsVal || !creditsLabel)
    return;

  function updateDisplay() {
    const value = parseInt(slider.value, 10) || 1;
    hoursVal.textContent = value;
    creditsVal.textContent = value;

    hoursLabel.textContent = value === 1 ? "Hour" : "Hours";
    creditsLabel.textContent = value === 1 ? "Time Credit" : "Time Credits";
  }

  slider.addEventListener("input", updateDisplay);
  updateDisplay();
}
