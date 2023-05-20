/**
 * The way to get things of URL.
 * @date 5/19/2023 - 11:46:41 AM
 *
 * @type {URLSearchParams}
 */
const parameters = new URLSearchParams(window.location.search);
/**
 * UUID to be get from URL.
 * @date 5/19/2023 - 11:52:27 AM
 *
 * @type {string}
 */
const tokenParameter = parameters.get("token");
/**
 * Product identification number to be get from URL.
 * @date 5/19/2023 - 12:47:07 PM
 *
 * @type {number}
 */
const idParameter = parameters.get("id");
/**
 * It will check if this product was liked and/or marked as favourite?
 * @date 5/19/2023 - 12:11:24 PM
 *
 * @type {boolean}
 */
let verify;
const productDiv = document.getElementById("product-container");
const productLink = document.getElementById("product-link");
const productImage = document.getElementById("product-image");
const productName = document.getElementById("product-name");
const productPrice = document.getElementsByClassName("price")[0];
const productReputation = document.getElementsByClassName("likes")[0];
const productSection = document.getElementById("product-section");
const reputationData = document.getElementById("reputation-data");
const likeLink = document.getElementById("like-link");
const likeIcon = document.getElementsByClassName("fa-thumbs-up")[0];
const favouriteLink = document.getElementById("favourite-link");
const favouriteIcon = document.getElementsByClassName("fa-heart")[0];
const addToCart = document.getElementsByClassName("btn")[0];
const addComment = document.getElementById("add-comment");

// Get the modal
let modal = document.getElementById("modal-comment");
// Get the <span> element that closes the modal
let span = document.getElementsByClassName("close")[0];

window.onload = () => {
    getProductData();
};

// When the user clicks on <span> (x), close the modal
span.onclick = function() {
    modal.style.display = "none";
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
    if (event.target == modal) {
      modal.style.display = "none";
    }
}

const getProductData = async () => {
    verify = (tokenParameter == NOT_LOGGED_TOKEN) ? false : true;

    const urlWithQueryParametersProduct = new URL(urlBase + "/product/by");
    urlWithQueryParametersProduct.searchParams.append("verify", verify);
    urlWithQueryParametersProduct.searchParams.append("id", idParameter);

    await fetch(
        urlWithQueryParametersProduct,
        fetchContentFactoryWithoutBody(requestMethods.GET, tokenParameter)
    ).then((response) => {
        if (response.ok) {
            return response.json();
        }
    }).then((product) => {
        // <a href="../html/product_details.html">
        const productLink = document.createElement("a");
        productLink.href = "#";
        // <img src="../images/whey-protein.webp" alt=""></a>
        const productImage = document.createElement("img");
        productImage.src = product.image;
        productImage.alt = product.name;
        // <h3>whey gold standard</h3>
        const productName = document.createElement("h3");
        productName.innerText = product.name;
        // <div class="price">10.99€</div>
        const productPrice = document.createElement("div");
        productPrice.classList.add("price");
        productPrice.innerText = product.price;
        // <div class="likes"></div>
        const productReputation = document.createElement("div");
        productReputation.classList.add("likes");
        // <p>beauty</p>
        const productSection = document.createElement("p");
        productSection.innerText = product.section;
        // <div>30 </div>
        const reputationData = document.createElement("div");
        reputationData.id = "reputation-data";
        reputationData.innerHTML = product.totalLikes + "&ensp;";
        // <a href="#">
        const likeLink = document.createElement("a");
        likeLink.href = "#";
        // <i class="fa-regular fa-thumbs-up"></i>
        const likeIcon = document.createElement("i");

        if (tokenParameter == NOT_LOGGED_TOKEN || !product.hasLoggedUserLiked) {
			likeIcon.classList.remove("fa-solid");
			likeIcon.classList.add("fa-regular");
		} else {
			likeIcon.classList.remove("fa-regular");
			likeIcon.classList.add("fa-solid");
		}
		likeIcon.classList.add("fa-thumbs-up");
        
        // <a href="#"></a>
        const favoriteLink = document.createElement("a");
        favoriteLink.href = "#";

        // <i class="fa-solid fa-heart"></i>
        const favoriteIcon = document.createElement("i");

        if (tokenParameter == NOT_LOGGED_TOKEN || !product.hasLoggedUserFavorited) {
			favoriteIcon.classList.remove("fa-solid");
			favoriteIcon.classList.add("fa-regular");
		} else {
			favoriteIcon.classList.remove("fa-regular");
			favoriteIcon.classList.add("fa-solid");
		}
		favoriteIcon.classList.add("fa-heart");

        // TODO: Se esse usuário já tiver comentado este produto, carrega o solid, senão, o regular
        // <i class="fa-solid fa-comment"></i>
        const commentIcon = document.createElement("i");
        commentIcon.classList.add("fa-solid");
        commentIcon.classList.add("fa-comment");

        // <a href="#" class="btn">add to cart</a>
        const addToCart = document.createElement("a");
        addToCart.href = "#";
        addToCart.classList.add("btn");
        addToCart.innerText = "add to cart";

        // <a href="#products" id="add-comment" class="btn">add comment</a>
        const addComment = document.createElement("a");
        addComment.href ="#";
        addComment.id = "add-comment";
        addComment.classList.add("btn");
        addComment.innerText = "add comment";
        // When the user clicks the button, open the modal 
        addComment.onclick = function() {
            modal.style.display = "block";
        }

        likeLink.appendChild(likeIcon);
        favoriteLink.appendChild(favoriteIcon);
        reputationData.appendChild(likeLink);
        // reputationData.innerHTML = "&emsp;"
        // TODO: reputationData.innerText = Quantidade de comentários desse produto
        reputationData.appendChild(favoriteLink);
        reputationData.appendChild(commentIcon);
        productReputation.appendChild(productSection);
        productReputation.appendChild(reputationData);
        productLink.appendChild(productImage);
        productDiv.appendChild(productLink);
        productDiv.appendChild(productName);
        productDiv.appendChild(productPrice);
        productDiv.appendChild(productReputation);
        productDiv.appendChild(addToCart);
        productDiv.appendChild(addComment);
        
    });
};