let searchForm = document.querySelector(".search-form");
document.querySelector("#search-btn").onclick = () => {
	searchForm.classList.toggle("active");
	shoppingCart.classList.remove("active");
	signinForm.classList.remove("active");
	navbar.classList.remove("active");
};

let shoppingCart = document.querySelector(".shopping-cart");
document.querySelector("#cart-btn").onclick = () => {
	searchForm.classList.remove("active");
	shoppingCart.classList.toggle("active");
	signinForm.classList.remove("active");
	navbar.classList.remove("active");
};

let signinForm = document.querySelector(".signin-form");
document.querySelector("#signin-btn").onclick = () => {
	searchForm.classList.remove("active");
	shoppingCart.classList.remove("active");
	signinForm.classList.toggle("active");
	navbar.classList.remove("active");
};

let navbar = document.querySelector(".navbar");
document.querySelector("#menu-btn").onclick = () => {
	searchForm.classList.remove("active");
	shoppingCart.classList.remove("active");
	signinForm.classList.remove("active");
	navbar.classList.toggle("active");
};

let dashboardBtn = document.getElementById("dashboard-btn");
dashboardBtn.addEventListener("click", function () {
	window.location.href = "dashboard.html";
});

window.onscroll = () => {
	searchForm.classList.remove("active");
	shoppingCart.classList.remove("active");
	signinForm.classList.remove("active");
	navbar.classList.remove("active");
};

var swiper = new Swiper(".product-slider", {
	loop: true,
	spaceBetween: 20,
	autoplay: {
		delay: 7500,
		disableOnInteraction: false,
	},
	breakpoints: {
		0: {
			slidesPerView: 1,
		},
		768: {
			slidesPerView: 2,
		},
		1020: {
			slidesPerView: 3,
		},
	},
});

var swiper = new Swiper(".review-slider", {
	loop: true,
	spaceBetween: 20,
	autoplay: {
		delay: 7500,
		disableOnInteraction: false,
	},
	breakpoints: {
		0: {
			slidesPerView: 1,
		},
		768: {
			slidesPerView: 2,
		},
		1020: {
			slidesPerView: 3,
		},
	},
});
