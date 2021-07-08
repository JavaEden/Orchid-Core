window.toggleTag = function (id, tag) {
    let isTurningOnFilter = true;

    Array.prototype.forEach.call(
        document.querySelectorAll(`#${id} [data-tag]`),
        function (el, i) {
            let itemTags = el.getAttribute("data-tag");

            if (itemTags === tag) {
                isTurningOnFilter = !el.classList.contains("is-primary");
                if(isTurningOnFilter) {
                    el.classList.remove("is-light");
                    el.classList.add("is-primary");
                }
                else {
                    el.classList.add("is-light");
                    el.classList.remove("is-primary");
                }
            } else {
                el.classList.add("is-light");
                el.classList.remove("is-primary");
            }
        });

    Array.prototype.forEach.call(
        document.querySelectorAll(`#${id} [data-tags]`),
        function (el, i) {
            let itemTags = el.getAttribute("data-tags").split(",");

            if(isTurningOnFilter) {
                if (itemTags.includes(tag)) {
                    el.classList.remove("is-gone")
                } else {
                    el.classList.add("is-gone")
                }
            }
            else {
                el.classList.remove("is-gone")
            }
        });
};
