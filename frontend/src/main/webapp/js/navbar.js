/**
 * HTML <strong><em>nav</em></strong> element that holds the site menu.
 * @date 5/23/2023 - 3:25:40 PM
 *
 * @type {HTMLElement}
 */
let navbar = document.querySelector("navbar");
document.querySelector("#menu-btn").onclick = () => {
	navbar.classList.toggle("active");
};

window.onscroll = () => {
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
