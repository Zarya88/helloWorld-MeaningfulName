<script>
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





</script>

<main>
whatever
<form>
    <label for="username">Username:</label>
    <input name="username" type="text" bind:value={name} />
    <label for="email">Email:</label>
    <input name="email" type="text" bind:value={email} />
    <button class="redbutton" type="button" onclick={createUser}> BUTTON </button>
</form>
    <pee>{JSON.stringify(user)}</pee>
</main>

<style>
    .redbutton {
        background-color:red;
    }
</style>
