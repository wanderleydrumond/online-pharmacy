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
const saveComment = document.getElementById("save-comment");

// Get the modal
let modal = document.getElementById("modal-comment");
// Get the <span> element that closes the modal
let span = document.getElementsByClassName("close")[0];
let isNewComment;
const inputComment = document.getElementById("comment-text");
const commentsDiv = document.getElementById("comments-container");
let hasCommented = false;
let commentJson = {
    id: 0,
    text: ""
}

window.onload = async () => {
    await getComments();
    getProductData();
};

// When the user clicks on <span> (x), close the modal
span.onclick = function () {
    modal.style.display = "none";
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function (event) {
    if (event.target == modal) {
        modal.style.display = "none";
    }
}

// PRODUCT

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
        loadProduct(product);
    });
};

const loadProduct = (product) => {
    while (productDiv.children.length > 0) {
        productDiv.removeChild(productDiv.children[0]);
    }

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
    /*const addToCart = document.createElement("a");
    addToCart.href = "#";
    addToCart.classList.add("btn");
    addToCart.innerText = "add to cart";*/

    // <a href="#products" id="add-comment" class="btn">add comment</a>
    const addComment = document.createElement("a");
    addComment.href = "#";
    addComment.id = "add-comment";
    addComment.classList.add("btn");
    addComment.innerText = "add comment";
    if (hasCommented) {
        addComment.classList.add("disappear");
    }
    // When the user clicks the button, open the modal 
    addComment.onclick = function () {
        isNewComment = true;
        modal.style.display = "block";
    };

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
    // productDiv.appendChild(addToCart);
    productDiv.appendChild(addComment);

    if (tokenParameter != NOT_LOGGED_TOKEN) {
        likeLink.onclick = async () => {
            if (!product.hasLoggedUserLiked) {
                const urlWithQueryParametersLike = new URL(urlBase + "/product/like");
                urlWithQueryParametersLike.searchParams.append("id", product.id);

                await fetch(
                    urlWithQueryParametersLike,
                    fetchContentFactoryWithoutBody(requestMethods.PUT, tokenParameter)
                )
                    .then((response) => {
                        if (response.ok) {
                            getProductData();
                            productDiv.scrollIntoView({ behavior: "instant", block: "center" });
                        }
                    });
            } else {
                const urlWithQueryParametersUnlike = new URL(
                    urlBase + "/product/unlike"
                );
                urlWithQueryParametersUnlike.searchParams.append("id", product.id);

                await fetch(
                    urlWithQueryParametersUnlike,
                    fetchContentFactoryWithoutBody(requestMethods.PUT, tokenParameter)
                )
                    .then((response) => {
                        if (response.ok) {
                            getProductData();
                            productDiv.scrollIntoView({ behavior: "instant", block: "center" });
                        }
                    });
            }
        };

        favoriteLink.onclick = async () => {
            if (!product.hasLoggedUserFavorited) {
                const urlWithQueryParametersAll = new URL(
                    urlBase + "/product/favourite"
                );
                urlWithQueryParametersAll.searchParams.append("id", product.id);

                await fetch(
                    urlWithQueryParametersAll,
                    fetchContentFactoryWithoutBody(requestMethods.PUT, tokenParameter)
                )
                    .then((response) => {
                        if (response.ok) {
                            getProductData();
                            productDiv.scrollIntoView({ behavior: "instant", block: "center" });
                        }
                    });
            } else {
                const urlWithQueryParametersAll = new URL(
                    urlBase + "/product/unfavourite"
                );
                urlWithQueryParametersAll.searchParams.append("id", product.id);

                await fetch(
                    urlWithQueryParametersAll,
                    fetchContentFactoryWithoutBody(requestMethods.PUT, tokenParameter)
                )
                    .then((response) => {
                        if (response.ok) {
                            getProductData();
                            productDiv.scrollIntoView({ behavior: "instant", block: "center" });
                        }
                    });
            }
        };
    }
}

// COMMENTS

const getComments = async () => {
    const urlWithQueryParametersComments = new URL(urlBase + "/comment/all-by");
    urlWithQueryParametersComments.searchParams.append("id", idParameter);

    await fetch(
        urlWithQueryParametersComments,
        fetchContentFactoryWithoutBody(requestMethods.GET)
    ).then((response) => {
        if (response.ok) {
            return response.json();
        }
    }).then((comments) => {
        let commentLoggedUser = comments.find(commentElement => commentElement.tokenOwner == tokenParameter);
        if (commentLoggedUser) {
            hasCommented = true;
        }
        loadComments(comments);
    });
};

const loadComments = (comments) => {
    while (commentsDiv.children.length > 0) {
        commentsDiv.removeChild(commentsDiv.children[0]);
    }

    if (comments.length > 0) {
        comments.forEach(commentElement => {
            // <div class="swiper-slide box"></div>
            const commentDiv = document.createElement("div");
            commentDiv.classList.add("swiper-slide");
            commentDiv.classList.add("box");
            // <h3>Nolan Grayson</h3>
            const commentOwner = document.createElement("h3");
            commentOwner.innerText = commentElement.nameOwner;
            // <div class="comment">Wonderful product, a bit oily, but it have a great cost-benefit. I strongly reconmend to all that have dry skin.</div>
            const commentContent = document.createElement("div");
            commentContent.classList.add("comment");
            commentContent.innerText = commentElement.content;
            // <a href="#" class="btn">edit</a>
            const editButton = document.createElement("a");
            editButton.href = "#";
            editButton.classList.add("btn");
            editButton.innerText = "edit";
            editButton.addEventListener('click', (event) => {
                inputComment.value = commentElement.content;
                modal.style.display = "block";
                commentJson.id = commentElement.id;
                commentJson.text = commentElement.content;
                isNewComment = false;
            });
            // <a href="#" class="btn">remove</a>
            const removeButton = document.createElement("a");
            removeButton.href = "#";
            removeButton.classList.add("btn");
            removeButton.innerText = "remove";
            removeButton.addEventListener('click', async (event) => {
                const urlWithQueryParametersProduct = new URL(urlBase + "/comment/by");
                urlWithQueryParametersProduct.searchParams.append("id", commentElement.id);

                await fetch(
                    urlWithQueryParametersProduct,
                    fetchContentFactoryWithoutBody(requestMethods.DELETE, tokenParameter)
                ).then((response) => {
                    if (response.ok) {
                        hasCommented = false;
                        document.getElementById("add-comment").classList.remove("disappear");
                        getComments();
                        commentsDiv.scrollIntoView({ behavior: "instant", block: "center" });
                    }
                });
            });

            commentDiv.appendChild(commentOwner);
            commentDiv.appendChild(commentContent);
            if (commentElement.tokenOwner == tokenParameter) {
                commentDiv.appendChild(editButton);
                commentDiv.appendChild(removeButton);
            }
            commentsDiv.appendChild(commentDiv);
        });
    }
};

saveComment.addEventListener('click', async (event) => {
    if (isNewComment) {
        const urlWithQueryParametersComment = new URL(urlBase + "/comment/create");
        urlWithQueryParametersComment.searchParams.append("id", idParameter);

        let body = {
            content: inputComment.value.trim()
        }

        await fetch(
            urlWithQueryParametersComment, fetchContentFactoryWithBody(requestMethods.POST, body, tokenParameter)
        ).then((response) => {
            if (response.ok) {
                inputComment.value = "";
                modal.style.display = "none";
                hasCommented = true;
                document.getElementById("add-comment").classList.add("disappear");
                commentsDiv.scrollIntoView({ behavior: "instant", block: "center" });
                return response.json();
            }
        }).then((newComment) => {
            getComments();
        });
    } else {
        const urlWithQueryParametersComment = new URL(urlBase + "/comment/by");
        urlWithQueryParametersComment.searchParams.append("id", commentJson.id);

        let body = {
            content: inputComment.value.trim()
        }

        await fetch(
            urlWithQueryParametersComment, fetchContentFactoryWithBody(requestMethods.PUT, body, tokenParameter)
        ).then((response) => {
            if (response.ok) {
                inputComment.value = "";
                modal.style.display = "none";
                commentsDiv.scrollIntoView({ behavior: "instant", block: "center" });
                return response.json();
            }
        }).then((updatedComment) => {
            getComments();
        });
    }
});