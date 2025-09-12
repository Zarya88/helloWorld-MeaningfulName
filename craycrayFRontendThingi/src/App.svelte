<script>
    let currentPage = $state("home"); // "/home" | "/user/create"

    let name = $state("")
    let email = $state("")
    let user = $state({})

    async function createUser() {
        //console.log("hello")
        const response = await fetch("http://localhost:8080/user/create", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                "username": name,
                "email": email
            })
        })
        user=await response.json()
    }

    function goCreate() { currentPage = "createUser"; }
    function goHome()   { currentPage = "home"; }
</script>

<main>
    {#if currentPage === "home"}
        <h2>Welcome Home!</h2>
        <p>Create a user to get started.</p>
        <button class="blue-button" onclick={goCreate}>
            Go to Create User
        </button>

    {:else if currentPage === "createUser"}
        <h2>Create User</h2>

        <form onsubmit={(e) => { e.preventDefault(); createUser(); }}>
            <label>Username:</label>
            <input name="username" type="text" bind:value={name} />

            <label>Email:</label>
            <input name="email" type="text" bind:value={email} />

            <!-- This button will now submit the form -->
            <button class="red-button" type="submit">CREATE</button>
        </form>
        <pre>{JSON.stringify(user)}</pre> <!--pee-->
        <button class="blue-button" onclick={goHome}>
            ← Back to Home
        </button>
    {/if}
</main>


<!--
<style>
    .red-button {
        background-color:red;
    }
</style>
-->

<style>
    main { max-width: 700px; margin: 40px auto; font-family: sans-serif; }
    .panel {
        background: #fff;
        border-radius: 12px;
        padding: 24px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    }
    form {
        display: grid;
        grid-template-columns: 100px 1fr;
        gap: 8px 12px;
        margin-bottom: 16px;
    }
    .red-button {
        grid-column: 1 / -1;
        background-color: red;
        color: white;
        border: none;
        border-radius: 8px;
        padding: 8px 14px;
    }
    .blue-button {
        background: #2563eb;
        color: white;
        border: none;
        border-radius: 8px;
        padding: 8px 14px;
        margin-top: 10px;
    }
</style>
