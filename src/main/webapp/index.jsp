<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HourGlass | Campus Skill Exchange Network</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/landing.css">
</head>
<body>

    <!-- Atmospheric Deep Navy Canvas -->
    <div class="bg-stage"></div>

    <div class="layout-wrapper">

        <!-- PRODUCT NAVBAR -->
        <header id="navbar">
            <a href="${pageContext.request.contextPath}/" class="brand-logo">
                <svg class="brand-icon" viewBox="0 0 100 100" fill="none">
                    <path d="M25 15H75V25L53 50L75 75V85H25V75L47 50L25 25V15Z" stroke="#ABD2FA" stroke-width="6" stroke-linejoin="round"/>
                    <path d="M35 25H65L50 42L35 25Z" fill="#FFAA00"/>
                    <path d="M35 75H65L50 58L35 75Z" fill="#FFAA00"/>
                </svg>
                <span>HourGlass</span>
            </a>

            <ul class="nav-menu">
                <li><a href="#how-it-works" class="nav-link">How it Works</a></li>
                <li><a href="#explore-skills" class="nav-link">Explore Skills</a></li>
                <li><a href="#time-credits" class="nav-link">Time Credits</a></li>
            </ul>

            <div class="nav-actions">
                <a href="${pageContext.request.contextPath}/login" class="btn-login">Log In</a>
                <a href="${pageContext.request.contextPath}/register" class="btn-cta">Get Started</a>
            </div>
        </header>

        <!-- MAIN HERO SECTION -->
        <main class="hero">
            
            <!-- Hero Product Copy -->
            <div class="hero-content">
                <div class="hero-badge animate-load-1">
                    <span class="hero-badge-dot"></span>
                    Campus Time Economy
                </div>

                <h1 class="hero-title animate-load-2">
                    Give an hour.<br>
                    <span class="hero-title-highlight">Get an hour.</span>
                </h1>

                <p class="hero-description animate-load-3">
                    HourGlass is a campus platform where students trade skills and academic help using time credits. Simple, direct, and monetary-free.
                </p>

                <div class="hero-actions animate-load-4">
                    <a href="${pageContext.request.contextPath}/register" class="btn-cta" style="padding: 0.85rem 1.8rem; font-size: 0.95rem;">Start Exchanging</a>
                    <a href="#how-it-works" class="btn-hero-secondary">How 1 Hour Works</a>
                </div>
            </div>

            <!-- Hero Physical 3D HourGlass Visual -->
            <div class="hourglass-viewport" id="hourglass-viewport">
                <div class="hourglass-stage" id="hourglass-stage">
                    
                    <!-- Frame Structure -->
                    <div class="hourglass-frame-top"></div>
                    <div class="hourglass-pillar pillar-left"></div>
                    <div class="hourglass-pillar pillar-right"></div>
                    <div class="hourglass-frame-bottom"></div>

                    <!-- Glass Chamber SVG Vector Body -->
                    <div class="glass-container">
                        <svg class="glass-body" viewBox="0 0 220 348" fill="none">
                            <defs>
                                <linearGradient id="glassReflect" x1="0%" y1="0%" x2="100%" y2="100%">
                                    <stop offset="0%" stop-color="#ABD2FA" stop-opacity="0.25"/>
                                    <stop offset="40%" stop-color="#ABD2FA" stop-opacity="0.05"/>
                                    <stop offset="100%" stop-color="#DC95FF" stop-opacity="0.15"/>
                                </linearGradient>
                            </defs>
                            <path d="M20 10H200C200 10 180 120 125 160C118 165 118 183 125 188C180 228 200 338 200 338H20C20 338 40 228 95 188C102 183 102 165 95 160C40 120 20 10 20 10Z" 
                                  fill="url(#glassReflect)" 
                                  stroke="rgba(171, 210, 250, 0.3)" 
                                  stroke-width="3"/>
                            <path d="M35 25C35 25 48 100 85 135" stroke="rgba(255, 255, 255, 0.25)" stroke-width="2" stroke-linecap="round"/>
                        </svg>
                    </div>

                    <!-- Animated Sand Canvas Layer -->
                    <canvas id="sand-canvas"></canvas>

                </div>
            </div>

        </main>

        <!-- PRODUCT HOW IT WORKS SECTION (SCROLL REVEAL) -->
        <section class="product-section scroll-reveal" id="how-it-works">
            <div style="text-align: center; max-width: 650px; margin: 0 auto;">
                <p style="color: var(--color-amber); font-size: 0.85rem; font-weight: 700; letter-spacing: 0.15em; text-transform: uppercase; margin-bottom: 0.75rem;">Reciprocal Economy</p>
                <h2 style="font-size: 2.2rem; font-weight: 800; color: var(--text-primary); margin-bottom: 1rem;">1 hour of help = 1 Time Credit.</h2>
                <p style="color: var(--text-secondary); font-size: 1.05rem;">No money involved. Teach what you know, earn credit, and spend it when you need help from peers.</p>
            </div>

            <div class="section-grid">
                <div class="feature-block">
                    <div class="feature-icon-box">1</div>
                    <h3 class="feature-title">Share Your Skill</h3>
                    <p class="feature-desc">List subjects or skills you can help peers with, from calculus to coding or language practice.</p>
                </div>
                <div class="feature-block">
                    <div class="feature-icon-box">2</div>
                    <h3 class="feature-title">Earn Time Credits</h3>
                    <p class="feature-desc">Complete a 1-on-1 peer session. 60 minutes spent earns 1 Time Credit directly to your balance.</p>
                </div>
                <div class="feature-block">
                    <div class="feature-icon-box">3</div>
                    <h3 class="feature-title">Request Assistance</h3>
                    <p class="feature-desc">Use your earned credits to get help from experts in subjects where you want to grow.</p>
                </div>
            </div>
        </section>

        <!-- EXPLORE SKILLS SECTION -->
        <section class="product-section scroll-reveal" id="explore-skills">
            <div style="text-align: center; max-width: 650px; margin: 0 auto;">
                <p style="color: var(--color-cyan); font-size: 0.85rem; font-weight: 700; letter-spacing: 0.15em; text-transform: uppercase; margin-bottom: 0.75rem;">Peer Exchange Network</p>
                <h2 style="font-size: 2.2rem; font-weight: 800; color: var(--text-primary); margin-bottom: 0.5rem;">Explore Skills</h2>
                <p style="color: var(--text-secondary); font-size: 1.05rem; font-style: italic;">"Knowledge moves when students do."</p>
            </div>

            <div class="skills-grid">
                <div class="skill-card">
                    <div class="skill-icon">&lt;/&gt;</div>
                    <h3 class="skill-name">Coding</h3>
                    <p class="skill-desc">Algorithms, Data Structures, Java & Python peer tutoring.</p>
                </div>
                <div class="skill-card">
                    <div class="skill-icon">&sum;</div>
                    <h3 class="skill-name">Mathematics</h3>
                    <p class="skill-desc">Calculus, Linear Algebra, Statistics & Calculus help.</p>
                </div>
                <div class="skill-card">
                    <div class="skill-icon">&AElig;</div>
                    <h3 class="skill-name">Languages</h3>
                    <p class="skill-desc">Conversational practice, TOEFL prep & grammar coaching.</p>
                </div>
                <div class="skill-card">
                    <div class="skill-icon">&fnof;</div>
                    <h3 class="skill-name">Design</h3>
                    <p class="skill-desc">UI/UX prototyping, Figma basics & Graphic Design feedback.</p>
                </div>
                <div class="skill-card">
                    <div class="skill-icon">&infin;</div>
                    <h3 class="skill-name">Web Development</h3>
                    <p class="skill-desc">HTML/CSS, JavaScript frameworks & full-stack guidance.</p>
                </div>
                <div class="skill-card">
                    <div class="skill-icon">&para;</div>
                    <h3 class="skill-name">Academic Help</h3>
                    <p class="skill-desc">Essay proofreading, research methodology & study strategies.</p>
                </div>
            </div>
        </section>

        <!-- TIME CREDITS INTERACTIVE CALCULATOR SECTION -->
        <section class="product-section scroll-reveal" id="time-credits">
            <div style="text-align: center; max-width: 650px; margin: 0 auto 2.5rem auto;">
                <p style="color: var(--color-amber); font-size: 0.85rem; font-weight: 700; letter-spacing: 0.15em; text-transform: uppercase; margin-bottom: 0.75rem;">Time Economy Calculator</p>
                <h2 style="font-size: 2.2rem; font-weight: 800; color: var(--text-primary); margin-bottom: 0.5rem;">Your Time Has Value</h2>
                <p style="color: var(--text-secondary); font-size: 1.05rem;">Every hour you give becomes a Time Credit you can use when you need help.</p>
            </div>

            <div class="credit-calculator-card">
                <div class="credit-display-group">
                    <div class="credit-value-box">
                        <span id="slider-hours-val" class="credit-number">1</span>
                        <span id="slider-hours-label" class="credit-label">Hour</span>
                    </div>

                    <div class="credit-equals">=</div>

                    <div class="credit-value-box highlight">
                        <span id="slider-credits-val" class="credit-number">1</span>
                        <span id="slider-credits-label" class="credit-label">Time Credit</span>
                    </div>
                </div>

                <div class="slider-control-wrapper">
                    <input type="range" id="time-credit-slider" min="1" max="10" value="1" step="1" aria-label="Time credit range selector">
                    <div class="slider-ticks">
                        <span>1h</span>
                        <span>2h</span>
                        <span>3h</span>
                        <span>4h</span>
                        <span>5h</span>
                        <span>6h</span>
                        <span>7h</span>
                        <span>8h</span>
                        <span>9h</span>
                        <span>10h</span>
                    </div>
                </div>

                <p class="credit-explanation">Give an hour of help. Earn an hour of time.</p>
                <p class="credit-disclaimer">Illustration only &mdash; actual Time Credits are transferred after a verified exchange.</p>
            </div>
        </section>

    </div>

    <script src="${pageContext.request.contextPath}/js/landing.js"></script>
</body>
</html>