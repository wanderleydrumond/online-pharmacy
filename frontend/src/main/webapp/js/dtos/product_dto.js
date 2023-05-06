export class ProductDTO {
    id;
    totalLikes;
    name;
    image;
    section;
    price;
    hasLoggedUserLiked;
    hasLoggedUserLikedFavorited;

    constructor() {};
    constructor(name, image, section, price) {
        this.name = name;
        this.image = image;
        this.section = section;
        this.price = price;
    };
}